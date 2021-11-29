# DSL4j library

LingvoDSL is one of popular dictionary formats.
DSL4j is a parser library for LingvoDSL dictionary.

DSL4j supports `.dsl` file and also `.dsl.dz` compressed file.
DSL4j loads all dictionary data into memory. Please care
for memory consumption.

## Development status

A status of library development is considered as `Alpha`.

## How to use

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

```java
DslDictionary dslDictionary = DslDictionary.loadData(file);
DslResult dslResult = dslDictionary.lookup(word);
for (Entry<String,String> entry: dslResult.getEntries(htmlDslVisitor)) {
    String key = entry.getKey();
    String article = entry.getValue();
}
```

or with java8 stream;

```java
DslDictionary dslDictionary = DslDictionary.loadData(file);
List<String> result =
    dslDictionary.lookupPredictive(word).getEntries(htmlDslVisitor).stream()
            .map(e -> {"<strong>" + e.getKey() + "</strong><br/>" + e.getValue() + "</brPredictive(word).getEntries(htmlDslVisitor).stream()
        .map(e -> e.getKey() + e.getValue())
        .collect(Coll>"})
            .collect(Collectors.toList());
```

Please check `DslDictionaryTest` cases for visitors differences.

## Limitations

As of v0.2.0, DSL4j does not handle media tags for image, sound and video specially.
DSL4j just handle `[s]` and `[video]` as a normal tag, and pass file name as
ordinal text. Standard visitors just ignore these tags.

## License

DSL4J is distributed under GNU General Public License version 3 or (at your option) any later version.
