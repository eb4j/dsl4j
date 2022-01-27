/*
 * DSL4J, a parser library for LingoDSL format.
 * Copyright (C) 2021 Hiroshi Miura.
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

import java.util.HashMap;
import java.util.Map;

public final class LanguageName extends HashMap<String, String> {
    /**
     * Constructor.
     */
    public LanguageName() {
        super();
        super.put("Afrikaans", "af");
        super.put("Basque", "eu");
        super.put("Belarusian", "be");
        super.put("Bulgarian", "bg");
        super.put("Chinese", "zh_CN");
        super.put("ChinesePRC", "zh_TW");
        super.put("Czech", "cs");
        super.put("Danish", "da");
        super.put("Dutch", "nl");
        super.put("English", "en");
        super.put("Finnish", "fi");
        super.put("French", "fr");
        super.put("German", "de");
        super.put("GermanNewSpelling", "de");
        super.put("Greek", "el");
        super.put("Hungarian", "hu");
        super.put("Icelandic", "is");
        super.put("Indonesian", "id");
        super.put("Italian", "it");
        super.put("Kazakh", "kk");
        super.put("Latin", "la");
        super.put("Latvian", "lv");
        super.put("Lithuanian", "lt");
        super.put("NorwegianBokmal", "nb");
        super.put("NorwegianNynorsk", "nn");
        super.put("Polish", "pl");
        super.put("Portuguese", "pt");
        super.put("Romanian", "ro");
        super.put("Russian", "ru");
        super.put("SerbianCyrillic", "sr");
        super.put("Slovak", "sk");
        super.put("Slovenian", "sl");
        super.put("SpanishModernSort", "es");
        super.put("SpanishTraditionalSort", "es");
        super.put("Swahili", "sw");
        super.put("Swedish", "sv");
        super.put("Tatar", "tt");
        super.put("Turkish", "tr");
        super.put("Ukrainian", "uk");
    }

    /** Make this unmodifiable. */
    @Override
    public String put(final String key, final String value) {
        throw new UnsupportedOperationException();
    }
    /** Make this unmodifiable. */
    @Override
    public String remove(final Object key) {
        throw new UnsupportedOperationException();
    }
    /** Make this unmodifiable. */
    @Override
    public void putAll(final Map<? extends String, ? extends String> m) {
        throw new UnsupportedOperationException();
    }
    /** Make this unmodifiable. */
    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }
}
