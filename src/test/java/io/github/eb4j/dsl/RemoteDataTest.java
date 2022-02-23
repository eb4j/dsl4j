package io.github.eb4j.dsl;

import io.github.eb4j.dsl.visitor.DumpDslVisitor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Disabled
public class RemoteDataTest {

    private static Path testData;
    private static Path indexData;

    static void downloadTestData(final URL url, final Path destination) throws IOException {
        ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
        FileOutputStream fileOutputStream = new FileOutputStream(destination.toFile());
        fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
    }

    @BeforeAll
    static void preUp(@TempDir final Path tempDir) throws IOException {
        testData = tempDir.resolve("en_US_ipa.dsl.dz");
        indexData = tempDir.resolve("en_US_ipa.dsl.idx");
        URL source = new URL("https://github.com/open-dsl-dict/ipa-dict-dsl/releases/download/1.0/en_US_ipa.dsl.dz");
        downloadTestData(source, testData);
    }

    @Test
    @Order(1)
    void ipaDictDslTest() throws IOException {
        DslDictionary dictionary = DslDictionary.loadDictionary(testData, indexData, false);
        assertEquals("IPA Dictionary - English", dictionary.getDictionaryName());
        assertEquals("English", dictionary.getIndexLanguage());
        assertEquals("English", dictionary.getContentLanguage());
        DumpDslVisitor dumper = new DumpDslVisitor();
        Map.Entry<String, String> entry = dictionary.lookup("ace").getEntries(dumper).get(0);
        assertEquals("ace", entry.getKey());
        assertEquals("[m1]/ˈeɪs/[/m]\n", entry.getValue());
        entry = dictionary.lookup("analysis").getEntries(dumper).get(0);
        assertEquals("analysis", entry.getKey());
        assertEquals("[m1]/əˈnæɫəsəs/, /əˈnæɫɪsɪs/[/m]\n", entry.getValue());
        entry = dictionary.lookup("base").getEntries(dumper).get(0);
        assertEquals("base", entry.getKey());
        assertEquals("[m1]/ˈbeɪs/[/m]\n", entry.getValue());
        entry = dictionary.lookup("dictionaries").getEntries(dumper).get(0);
        assertEquals("dictionaries", entry.getKey());
        assertEquals("[m1]/ˈdɪkʃəˌnɛɹiz/[/m]\n", entry.getValue());
        entry = dictionary.lookup("space").getEntries(dumper).get(0);
        assertEquals("space", entry.getKey());
        assertEquals("[m1]/ˈspeɪs/[/m]\n", entry.getValue());
    }
}
