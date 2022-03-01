# DSL4j library

Lingvo DSL is an one of popular dictionary formats.
DSL4j is a parser library of Lingvo DSL dictionary file for Java.

DSL4j supports `.dsl` file and also `.dsl.dz` compressed file.
DSL4j loads all dictionary data and parse its index into memory.
An index will be saved in index file specified in second argument of `DslDictionary#loadDictionary` method.
When a specified index file exists and validated as up-to-date, dsl4j load the index instead of parsing dictionary.

NOTICE: Current version does not support media archive file `...dsl.files.zip` that is supported by GoldenDict.


## Supported dictionary format

Lingvo DSL format specification is a little ambiguous, and there are many variations of data in the wild.
Here is a table to show what variations are supported.

### v0.5.0

|    | Encoding | BOM | Line/record terminators  | Note                     |
| -- | -------- | --- | ------------------------ | ------------------------ |
| ✓  | UTF-16LE | Yes | CR+LF / empty line       |                          |
| ✓  | UTF-16LE | Yes | CR+LF / single CR+LF     |                          |
| ✓  | CP1251   | No  | CR+LF / empty line       | CODEPAGE header required |
| ✓  | CP1252   | No  | CR+LF / empty line       | CODEPAGE header required |
| ✓  | CP1253   | No  | CR+LF / empty line       | CODEPAGE header required |

#### Non standard combinations

|    | Encoding | BOM | Line/record terminators  | Note                     |
| -- | -------- | --- | ------------------------ | ------------------------ |
| ✓  | UTF-16LE | Yes | LF / empty line          |                          |
| ✓  | UTF-16LE | Yes | LF / single LF           |                          |
| ✓  | UTF-16LE | No  | LF / empty line          |                          |
| ?  | UTF-16LE | No  | LF / single LF           |                          |
| ✓  | UTF-8    | Yes | LF / empty line          |                          |
| ✓  | UTF-8    | No  | LF / empty line          |                          |
| ✓  | UTF-8    | Yes | LF / single LF           |                          |
| ✓  | UTF-8    | No  | LF / single LF           |                          |
| ✓  | CP1251   | No  | LF / empty line          | CODEPAGE header required |
| ✓  | CP1252   | No  | LF / empty line          | CODEPAGE header required |
| ✓  | CP1253   | No  | LF / empty line          | CODEPAGE header required |


### v0.4.5

|    | Encoding | BOM | Line/record terminators  | Note                     |
| -- | -------- | --- | ------------------------ | ------------------------ |
| ✓  | UTF-16LE | Yes | CR+LF / empty line       |                          |
| ❌  | UTF-16LE | Yes | CR+LF / single CR+LF     |                          |
| ✓  | CP1251   | No  | CR+LF / empty line       | CODEPAGE header required |
| ✓  | CP1252   | No  | CR+LF / empty line       | CODEPAGE header required |
| ✓  | CP1253   | No  | CR+LF / empty line       | CODEPAGE header required |

#### Non standard combinations                                                

|    | Encoding | BOM | Line/record terminators  | Note                     |
| -- | -------- | --- | ------------------------ | ------------------------ |
| ❌  | UTF-16LE | Yes | LF / single LF           |                          |
| ✓  | UTF-16LE | Yes | LF / empty line          |                          |
| ✓  | UTF-16LE | No  | LF / empty line          |                          |
| ?  | UTF-16LE | No  | LF / single LF           |                          |
| ?  | UTF-8    | Yes | LF / empty line          |                          |
| ✓  | UTF-8    | No  | LF / empty line          |                          |
| ❌  | UTF-8    | Yes | LF / single LF           |                          |
| ❌  | UTF-8    | No  | LF / single LF           |                          |
| ✓  | CP1251   | No  | LF / empty line          | CODEPAGE header required |
| ✓  | CP1252   | No  | LF / empty line          | CODEPAGE header required |
| ✓  | CP1253   | No  | LF / empty line          | CODEPAGE header required |


### Unsupported DSL syntax

There are several syntax that DSL4j cannot handle.
These syntax characters are passed as-is.

- Unsorted part of head word with braces
- Tilda word replacement
- Alternative heading section with (...)
- Subentry @ mark
- Reference to another card entry
- Comments with double braces "{{comment}}" in article

## Development status

A status of library development is considered as `Beta`.


## Install

### Apache Maven

<details>

```xml
<dependency>
  <groupId>io.github.eb4j</groupId>
  <artifactId>dsl4j</artifactId>
  <version>0.4.5</version>
</dependency>
```

</details>

### Gradle Groovy DSL

<details>validateAbsolutePath? 

```groovy
implementation 'io.github.eb4j:dsl4j:0.4.5'
```
</details>

### Gradle kotlin DSL

<details>

```kotlin
implementation("io.github.eb4j:dsl4j:0.4.5")
```

</details>

### Scala SBT 

<details>

```
libraryDependencies += "io.github.eb4j" % "dsl4j" % "0.4.5"
```

</details>

## Use

DSL4j provide a DSL dictionary loader and an article parser.
You should call `DslDictionary#loadData` method to load DSL file.
The method return `DslDictionary` object that has methods
`lookup` and `lookupPredictive`. The former method search word,
and the latter is predictive, run prefix search for word.
These method returns `DslResult` object.

You need to prepare `DslVisitor` filter class.
DSL4j provide three standard visitor filter.

* HtmlDslVisitor: convert to HTML.
* PlainDslVisitor: convert to plain text which strip all tags.
* DumpDslVisitor: produce same content as input DSL (for debug).

`DslResult.getEntries(visitor)` returns `List<Map.Entry<String, T>>`
where `T` is decided by visitor.

### Example

Here is a simple example how to use it.

```java
public static void main(String... argv){
        Path dictionaryPath = Path.to(argv[1]);
        Path indexPath = Path.to(dictionaryPath + ".idx"); 
        String word = argv[2];
        DslDictionary dslDictionary = DslDictionary.loadDictionary(dictionaryPath,indexPath);
        PlainDslVisitor plainDslVisitor = new PlainDslVisitor();
        DslResult dslResult=dslDictionary.lookup(word);
        for (Map.Entry<String, String> entry: dslResult.getEntries(plainDslVisitor)){
            String key = entry.getKey();
            String article = entry.getValue();
            System.out.println(key, article);
        }
}
```

Here is pragmatic one with java8 streams, predictive search method and HTML converter;

```java
DslDictionary dslDictionary = DslDictionary.loadDictionary(file);
HtmlDslVisitor htmlDslVisitor = new HtmlDslVisitor(file.getParent());
List<String> result =
    dslDictionary.lookupPredictive(word).getEntries(htmlDslVisitor).stream()
            .map(e -> {"<p><strong>" + e.getKey() + "</strong>" + e.getValue() + "</p>"})
            .collect(Collectors.toList());
```

Please check `DslDictionaryTest` cases for visitors differences.

### index file

The index file format is DSL4j original but it is designed to be usable from other applications/libraries.
The index file is formatted and produced by Google's Protocol Buffers and compressed by gzip.

Definition of the index file is placed in `src/main/proto/DslIndex.proto` with ProtoBuf v3 source format.
Java sources are automatically generated at build time in `src/generated/main/java`

WARNING: The index file feature is in a status of `experimental`. It will be changed to break compatibility without
precaution notice.

### Language names and codes

DSL4j has immutable tables of language names and codes DSL supported.
You can get ISO639 language code from instance of `LanguageName` and `LanguageCode` class
that are immutable Map, like;

```java
LanguageCode languageCode = new LanguageCode();
LanguageName languageName = new LanguageName();
assert(languageCode.get(1).equals("en"));
assert(langaugeName.containsKey("Russian"));
```

These code may be aprear in `[lang id=?]` tag. When you want to write your own custom visitor
you may want to know it.

### Colors

DSL4j recognize HTML color names in lower case for `[c]` tag.

### Media tags

DSL4j does not handle media tags for image, sound and video specially.
DSL4j just handle `[s]` and `[video]` as a normal tag, and pass file name as
ordinal text.

Please see `HtmlDslVisitor` class to know how to handle these tags.

### DSL article syntax definition

DSL article parser is written using JavaCC parser generator and the definition is placed at
`src/main/java/io/github/eb4j/dsl/DslParser.jj`
Java sources are automatically generated at build time in `src/generated/main/java`

## Use cases

- OmegaT: A free and open source multiplatform Computer Assisted Translation tool
- EBViewer: Very simple dictionary search application

## License and copyright

DSL4J is distributed under GNU General Public License version 3 or (at your option) any later version.
Please see LICENSE file for details.

A part of the code is delivered from OmegaT.

Copyright (C) 2015-2016 Hiroshi Miura, Aaron Madlon-Kay
 
Copyright (C) 2021-2022 Hiroshi Miura
