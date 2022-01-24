import java.io.File
import java.io.FileInputStream
import java.util.Properties

plugins {
    checkstyle
    jacoco
    signing
    `java-library`
    `java-library-distribution`
    `maven-publish`
    id("com.intershop.gradle.javacc") version "4.0.1"
    kotlin("jvm") version "1.6.10"
    id("com.github.spotbugs") version "5.0.5"
    id("com.diffplug.spotless") version "6.0.5"
    id("com.github.kt3k.coveralls") version "2.12.0"
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
    id("com.palantir.git-version") version "0.13.0" apply false
}

group = "io.github.eb4j"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains:annotations:23.0.0")
    implementation("com.github.takawitter:trie4j:0.9.8")
    implementation("commons-io:commons-io:2.11.0")
    testImplementation("org.codehaus.groovy:groovy-all:3.0.9")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

spotbugs {
    excludeFilter.set(project.file("config/spotbugs/exclude.xml"))
    tasks.spotbugsMain {
        reports.create("html") {
            required.set(true)
        }
    }
    tasks.spotbugsTest {
        reports.create("html") {
            required.set(true)
        }
    }
}

jacoco {
    toolVersion="0.8.6"
}

tasks.jacocoTestReport {
    dependsOn(tasks.test) // tests are required to run before generating the report
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

coveralls {
    jacocoReportPath = "build/reports/jacoco/test/jacocoTestReport.xml"
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-Xlint:deprecation")
    options.compilerArgs.add("-Xlint:unchecked")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
    withSourcesJar()
    withJavadocJar()
}

tasks.withType<Javadoc> {
    exclude("io/github/eb4j/dsl/DslParser*",
            "io/github/eb4j/dsl/Token*",
            "io/github/eb4j/dsl/JavaCharStream.java",
            "io/github/eb4j/dsl/ParseException.java")
}

javacc {
    javaCCVersion = "7.0.10"
    configs.create("dsl") {
        inputFile = File("src/main/java/io/github/eb4j/dsl/DslParser.jj")
        outputDir = File("build/generated/javacc")
        packageName = "io.github.eb4j.dsl"
    }
}

// we handle cases without .git directory
val home = System.getProperty("user.home")
val javaHome = System.getProperty("java.home")
val props = project.file("src/main/resources/version.properties")
val dotgit = project.file(".git")

if (dotgit.exists()) {
    apply(plugin = "com.palantir.git-version")
    val versionDetails: groovy.lang.Closure<com.palantir.gradle.gitversion.VersionDetails> by extra
    val details = versionDetails()
    val baseVersion = details.lastTag.substring(1)
    if (details.isCleanTag) {  // release version
        version = baseVersion
    } else {  // snapshot version
        version = baseVersion + "-" + details.commitDistance + "-" + details.gitHash + "-SNAPSHOT"
    }

    publishing {
        publications {
            create<MavenPublication>("mavenJava") {
                from(components["java"])
                pom {
                    name.set("URL protocol handler")
                    description.set("LingoDSL parser for java")
                    url.set("https://github.com/eb4j/dsl4j")
                    licenses {
                        license {
                            name.set("The GNU General Public License, Version 3")
                            url.set("https://www.gnu.org/licenses/licenses/gpl-3.html")
                            distribution.set("repo")
                        }
                    }
                    developers {
                        developer {
                            id.set("miurahr")
                            name.set("Hiroshi Miura")
                            email.set("miurahr@linux.com")
                        }
                    }
                    scm {
                        connection.set("scm:git:git://github.com/eb4j/dsl4j.git")
                        developerConnection.set("scm:git:git://github.com/eb4j/dsl4j.git")
                        url.set("https://github.com/eb4j/dsl4j")
                    }
                }
            }
        }
    }

    signing {
        if (project.hasProperty("signingKey")) {
            val signingKey: String? by project
            val signingPassword: String? by project
            useInMemoryPgpKeys(signingKey, signingPassword)
        } else {
            useGpgCmd()
        }
        sign(publishing.publications["mavenJava"])
    }

    tasks.withType<Sign> {
        val hasKey = project.hasProperty("signingKey") || project.hasProperty("signing.gnupg.keyName")
        onlyIf { hasKey && details.isCleanTag }
    }

    nexusPublishing {
        repositories {
            sonatype {
                nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
                snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
                username.set(System.getenv("SONATYPE_USER"))
                password.set(System.getenv("SONATYPE_PASS"))
            }
        }
    }
} else if (props.exists()) { // when version.properties already exist, just use it.

    fun getProps(f: File): Properties {
        val props = Properties()
        try {
            props.load(FileInputStream(f))
        } catch (t: Throwable) {
            println("Can't read $f: $t, assuming empty")
        }
        return props
    }

    version = getProps(props).getProperty("version")
}

tasks.register("writeVersionFile") {
    val folder = project.file("src/main/resources")
    if (!folder.exists()) {
        folder.mkdirs()
    }
    props.delete()
    props.appendText("version=" + project.version)
}

tasks.getByName("jar") {
    dependsOn("writeVersionFile")
}
