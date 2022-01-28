package io.github.eb4j.dsl.data;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Arrays;

public class DslDictionaryProperty implements Serializable {
    private final String dictionaryName;
    private final String indexLanguage;
    private final String contentLanguage;
    private final String charset;
    private final byte[] eol;

    public DslDictionaryProperty(final String dictionaryName, final String indexLanguage, final String contentLanguage,
                                 final Charset charset, final byte[] eol) {
        this.dictionaryName = dictionaryName;
        this.indexLanguage = indexLanguage;
        this.contentLanguage = contentLanguage;
        this.charset = charset.name();
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
        return Charset.forName(charset);
    }

    public byte[] getEol() {
        return Arrays.copyOf(eol, eol.length);
    }
}
