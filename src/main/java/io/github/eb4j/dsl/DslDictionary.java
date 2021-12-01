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

package io.github.eb4j.dsl;

import io.github.eb4j.dsl.data.DictionaryData;
import io.github.eb4j.dsl.data.DictionaryDataBuilder;
import org.apache.commons.io.ByteOrderMark;
import org.apache.commons.io.input.BOMInputStream;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;


public class DslDictionary {
    private static final String[] PATTERNS = {"name", "index", "content", "codepage", "include"};
    private static final Pattern METAPATTERN = Pattern.compile(
                    "^#(NAME\\s(?<name>.+?)"
                    + "|INDEX_LANGUAGE\\s(?<index>.+?)"
                    + "|CONTENTS_LANGUAGE\\s(?<content>.+?)"
                    + "|SOURCE_CODE_PAGE\\s(?<codepage>.+?))"
                    + "|INCLUDE\\s(?<include>.+?)$");
    private static final String[] ALLOWED_CODE_PAGE = {"EasternEuropean", "Cyrillic", "Latin", "Greek", "Turkish"};
    private final DictionaryData<Object> dictionaryData;
    private final String dictionaryName;
    private final String indexLanguage;
    private final String contentLanguage;

    public DslDictionary(final DictionaryData<Object> dictionaryData, final String dictionaryName,
                         final String indexLanguage, final String contentLanguage) {
        this.dictionaryData = dictionaryData;
        this.dictionaryName = dictionaryName;
        this.indexLanguage = indexLanguage;
        this.contentLanguage = contentLanguage;
    }

    public DslResult lookup(final String word) {
        return new DslResult(dictionaryData.lookUp(word));
    }

    public DslResult lookupPredictive(final String word) {
         return new DslResult(dictionaryData.lookUpPredictive(word));
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

    public static DslDictionary loadDictionary(final File file) throws IOException {
        final Map<String, String> metadata = new HashMap<>();
        if (!file.isFile()) {
            throw new IOException("Target file is not a file.");
        }
        // Read header
        Charset charset;
        try (FileInputStream fis = new FileInputStream(file)) {
            // Un-gzip if necessary
            InputStream is;
            if (file.getName().endsWith(".dz")) {
                is = new GZIPInputStream(fis, 8192);
            } else {
                is = fis;
            }
            try (BOMInputStream bis = new BOMInputStream(is, false, ByteOrderMark.UTF_8, ByteOrderMark.UTF_16LE)) {
                // Detect codepage and charset
                if (!bis.hasBOM()) {
                    charset = StandardCharsets.ISO_8859_1;
                } else if (bis.hasBOM(ByteOrderMark.UTF_16LE)) {
                    charset = StandardCharsets.UTF_16LE;
                } else {
                    charset = StandardCharsets.UTF_8;
                }
                try (InputStreamReader isr = new InputStreamReader(bis, charset);
                     BufferedReader reader = new BufferedReader(isr)) {
                    String l = reader.readLine();
                    while (l != null && !l.isEmpty()) {
                        Matcher m = METAPATTERN.matcher(l);
                        if (!m.matches()) {
                            continue;
                        }
                        for (String pattern : PATTERNS) {
                            if (m.group(pattern) != null) {
                                String s = m.group(pattern);
                                if (s.startsWith("\"") && s.endsWith("\"")) {
                                    metadata.put(pattern, s.substring(1, s.length() - 1));
                                } else{
                                    metadata.put(pattern, m.group(pattern));
                                }
                                break;
                            }
                        }
                        l = reader.readLine();
                    }
                }
            }
        }
        // Reopen dictionary and build dictionary index
        DictionaryDataBuilder<Object> builder = new DictionaryDataBuilder<>();
        StringBuilder word = new StringBuilder();
        StringBuilder trans = new StringBuilder();
        try (FileInputStream fis = new FileInputStream(file)) {
            InputStream is;
            if (file.getName().endsWith(".dz")) {
                is = new GZIPInputStream(fis, 8192);
            } else {
                is = fis;
            }
            try (BOMInputStream bis = new BOMInputStream(is)) {
                // detect charset when it is not UNICODE
                if (charset == StandardCharsets.ISO_8859_1 && metadata.containsKey("codepage")) {
                    String codepageName = metadata.get("codepage");
                    for (int i = 0; i < ALLOWED_CODE_PAGE.length; i++) {
                        String name = ALLOWED_CODE_PAGE[i];
                        if (name.equals(codepageName)) {
                            charset = Charset.forName(String.format("Cp%4d", 1250 + i));
                            break;
                        }
                    }
                }
                try (InputStreamReader isr = new InputStreamReader(bis, charset);
                     BufferedReader reader = new BufferedReader(isr)) {
                    reader.lines()
                            .filter(line -> !line.isEmpty() && !line.startsWith("#"))
                            .forEach(line -> {
                                if (Character.isWhitespace(line.codePointAt(0))) {
                                    trans.append(line.trim()).append('\n');
                                } else {
                                    if (word.length() > 0) {
                                        builder.add(word.toString(), trans.toString());
                                        word.setLength(0);
                                        trans.setLength(0);
                                    }
                                    word.append(line);
                                }
                            });
                    if (word.length() > 0) {
                        builder.add(word.toString(), trans.toString());
                    }
                }
            }
        }
        DictionaryData<Object> data = builder.build();
        String name = metadata.get("name");
        String indexLang = metadata.get("index");
        String contentLang = metadata.get("content");
        return new DslDictionary(data, name, indexLang, contentLang);
    }
}
