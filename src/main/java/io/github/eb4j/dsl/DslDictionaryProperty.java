package io.github.eb4j.dsl;

import java.nio.charset.Charset;
import java.util.Arrays;

public class DslDictionaryProperty {
    private final String dictionaryName;
    private final String indexLanguage;
    private final String contentLanguage;
    private final Charset charset;
    private final byte[] eol;

    public DslDictionaryProperty(final String dictionaryName, final String indexLanguage, final String contentLanguage,
                                 final Charset charset, final byte[] eol) {
        this.dictionaryName = dictionaryName;
        this.indexLanguage = indexLanguage;
        this.contentLanguage = contentLanguage;
        this.charset = charset;
        this.eol = Arrays.copyOf(eol, eol.length);
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

    public byte[] getEol() {
        return Arrays.copyOf(eol, eol.length);
    }
}
