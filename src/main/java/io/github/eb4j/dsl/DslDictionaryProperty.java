package io.github.eb4j.dsl;

import java.nio.charset.Charset;

public class DslDictionaryProperty {
    private final String dictionaryName;
    private final String indexLanguage;
    private final String contentLanguage;
    private final Charset charset;

    public DslDictionaryProperty(final String dictionaryName, final String indexLanguage, final String contentLanguage,
                                 final Charset charset) {
        this.dictionaryName = dictionaryName;
        this.indexLanguage = indexLanguage;
        this.contentLanguage = contentLanguage;
        this.charset = charset;
    }

    public String getDictionaryName() {
        return dictionaryName;
    }

    public String getIndexLanguage() {
        return indexLanguage;
    }

    public String getContentLanguage() {
        return contentLanguage;
    }

    public Charset getCharset() {
        return charset;
    }
}
