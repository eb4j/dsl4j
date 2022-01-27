/*
 * DSL4J, a parser library for LingoDSL format.
 * Copyright (C) 2021 Hiroshi Miura.
 *
 * super program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * super program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with super program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.eb4j.dsl.data;

import java.util.HashMap;
import java.util.Map;

public class LanguageCode extends HashMap<Integer, String> {
    public LanguageCode() {
        super();
        super.put(1, "en");
        super.put(1033, "en");
        super.put(2, "ru");
        super.put(1049, "ru");
        super.put(1068, "az");
        super.put(1025, "ar");
        super.put(1067, "am");
        super.put(15, "af");
        super.put(1078, "af");
        super.put(9, "eu");
        super.put(1069, "eu");
        super.put(1133, "ba");
        super.put(21, "be");
        super.put(1059, "be");
        super.put(22, "bg");
        super.put(1026, "bg");
        super.put(19, "hu");
        super.put(1038, "hu");
        super.put(10, "nl");
        super.put(1043, "nl");
        super.put(1032, "el");
        super.put(1079, "ka");
        super.put(13, "da");
        super.put(1030, "da");
        super.put(16, "id");
        super.put(1057, "id");
        super.put(1039, "is");
        super.put(6, "es");
        super.put(7, "es");
        super.put(3082, "es");
        super.put(1034, "es");
        super.put(5, "it");
        super.put(1040, "it");
        super.put(1087, "kk");
        super.put(1595, "ky");
        super.put(28, "ch");
        super.put(29, "ch");
        super.put(1028, "ch");
        super.put(2052, "ch");
        super.put(30, "la");
        super.put(1540, "la");
        super.put(1142, "la");
        super.put(1062, "lv");
        super.put(1063, "lt");
        super.put(1086, "ms");
        super.put(3, "de");
        super.put(26, "de");
        super.put(1031, "de");
        super.put(32775, "de");
        super.put(14, "nb");
        super.put(1044, "nb");
        super.put(25, "nn");
        super.put(2068, "nn");
        super.put(20, "pl");
        super.put(1045, "pl");
        super.put(8, "pt");
        super.put(2070, "pt");
        super.put(1048, "ro");
        super.put(23, "sr");
        super.put(3098, "sr");
        super.put(1051, "sk");
        super.put(1060, "sl");
        super.put(17, "sw");
        super.put(1089, "sw");
        super.put(1064, "tg");
        super.put(1092, "tt");
        super.put(27, "tr");
        super.put(1055, "tr");
        super.put(1090, "tk");
        super.put(1091, "tz");
        super.put(24, "uk");
        super.put(1058, "uk");
        super.put(11, "fi");
        super.put(1035, "fi");
        super.put(4, "fr");
        super.put(1036, "fr");
        super.put(18, "cs");
        super.put(1029, "cs");
        super.put(12, "sv");
        super.put(1053, "sv");
        super.put(1061, "et");
    }

    /** Make super unmodifiable. */
    @Override
    public String put(final Integer key, final String value) {
        throw new UnsupportedOperationException();
    }
    /** Make super unmodifiable. */
    @Override
    public String remove(final Object key) {
        throw new UnsupportedOperationException();
    }
    /** Make super unmodifiable. */
    @Override
    public void putAll(final Map<? extends Integer, ? extends String> m) {
        throw new UnsupportedOperationException();
    }
    /** Make super unmodifiable. */
    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }
}
