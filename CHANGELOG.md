# Change Log
All notable changes to this project will be documented in this file.

## [Unreleased]

## [0.4.2]
* Fix bug raising exception when file end with double EOL terminator.
* Improve charset detection
  * Accept UTF-16LE without BOM
  * Accept UTF-8 without BOM
  * Accept UTF-16LE but terminator is LF-only.

## [0.4.1]
* DSL4j now read only head words and article positions when loading.
  This improve performance many and reduce memory consumption.
* Test with dictionary format variations, for encodings such as UTF-16LE, and Windows-1251
  and End-of-Line terminators, CR+LF or LF-only.
* Bump dependency
  * DictZip@0.11.1

## [0.3.0]
* Accept UTF-16, UTF-8 and ANSI files
  * can accept Cp1250, Cp1251 and Cp1252 codepages
* Refactoring DslArticle class
* Handles header properties
  * dictionary name
  * index language
  * contents langauge
* Support lang tag with name and id attribute
  * Provide acceptable LanguageName and LanguageCode map
* Improve standard HtmlDslVisitor

## [0.2.3]
* Fix parsing "]]" bracket
* Add support for "br" and "'" tag
* Improve url handling

## [0.2.2]
* Fix checkstyle warnings and firebugs error

## [0.2.1]
* HtmlDslVisitor: convert media tags to hyperlink or img tags

## [0.2.0]
* Add code examples in README
* Introduce visitor and data package
* Introduce DslResult class to integrate loader and parser
* Add HtmlDslVisitor
* Add PlainDslVisitor

## 0.1.0
* First internal release

[Unreleased]: https://github.com/eb4j/dsl4j/compare/v0.4.2...HEAD
[0.4.1]: https://github.com/eb4j/dsl4j/compare/v0.4.1...v0.4.2
[0.4.0]: https://github.com/eb4j/dsl4j/compare/v0.3.0...v0.4.1
[0.3.0]: https://github.com/eb4j/dsl4j/compare/v0.2.3...v0.3.0
[0.2.3]: https://github.com/eb4j/dsl4j/compare/v0.2.2...v0.2.3
[0.2.2]: https://github.com/eb4j/dsl4j/compare/v0.2.1...v0.2.2
[0.2.1]: https://github.com/eb4j/dsl4j/compare/v0.2.0...v0.2.1
[0.2.0]: https://github.com/eb4j/dsl4j/compare/v0.1.0...v0.2.0
