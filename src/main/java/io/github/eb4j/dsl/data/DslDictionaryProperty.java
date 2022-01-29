/*
 * DSL4J, a parser library for LingoDSL format.
 * Copyright (C) 2022 Hiroshi Miura.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package io.github.eb4j.dsl.data;

import java.nio.charset.Charset;
import java.util.Arrays;

public class DslDictionaryProperty {
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
