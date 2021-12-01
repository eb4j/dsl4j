# DSL4j library

LingvoDSL is one of popular dictionary formats.
DSL4j is a parser library for LingvoDSL dictionary.

DSL4j supports `.dsl` file and also `.dsl.dz` compressed file.
DSL4j loads all dictionary data into memory. Please care
for memory consumption.

## Development status

A status of library development is considered as `Alpha`.


## Install

### Apache Maven

<details>

```xml
<dependency>
  <groupId>io.github.eb4j</groupId>
  <artifactId>dsl4j</artifactId>
  <version>0.2.3</version>
</dependency>
```

</details>

### Gradle Groovy DSL

<details>

```groovy
implementation 'io.github.eb4j:dsl4j:0.2.3'
```
</details>

### Gradle kotlin DSL

<details>

```kotlin
implementation("io.github.eb4j:dsl4j:0.2.3")
```

</details>

### Scala SBT 

<details>

```
libraryDependencies += "io.github.eb4j" % "dsl4j" % "0.2.3"
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
DslDictionary dslDictionary = DslDictionary.loadData(file);
PlainDslVisitor plainDslVisitor = new PlainDslVisitor();
DslResult dslResult = dslDictionary.lookup(word);
for (Entry<String,String> entry: dslResult.getEntries(plainDslVisitor)) {
    String key = entry.getKey();
    String article = entry.getValue();
}
```

Here is pragmatic one with java8 streams, predictive search method and HTML converter;

```java
DslDictionary dslDictionary = DslDictionary.loadData(file);
HtmlDslVisitor htmlDslVisitor = new HtmlDslVisitor(file.getParent());
List<String> result =
    dslDictionary.lookupPredictive(word).getEntries(htmlDslVisitor).stream()
            .map(e -> {"<p><strong>" + e.getKey() + "</strong>" + e.getValue() + "</p>"})
            .collect(Collectors.toList());
```

Please check `DslDictionaryTest` cases for visitors differences.

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

### Colors

DSL4j recognize HTML color names in lower case for `[c]` tag.

### Media tags

DSL4j does not handle media tags for image, sound and video specially.
DSL4j just handle `[s]` and `[video]` as a normal tag, and pass file name as
ordinal text.

Please see `HtmlDslVisitor` class to know how to handle these tags.

## License and copyright

DSL4J is distributed under GNU General Public License version 3 or (at your option) any later version.
Please see LICENSE file for details.

A part of the code is delivered from OmegaT - computer assisted translation tool.

Copyright (C) 2015-2016 Hiroshi Miura, Aaron Madlon-Kay
 
Copyright (C) 2021 Hiroshi Miura
