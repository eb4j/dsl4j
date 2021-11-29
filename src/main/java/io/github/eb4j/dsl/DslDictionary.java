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

import org.apache.commons.io.input.BOMInputStream;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;


public class DslDictionary {
    private final DictionaryData<Object> dictionaryData;

    public DslDictionary(final DictionaryData<Object> data) {
        dictionaryData = data;
    }

    public DslResult getEntries(final String word) {
        return new DslResult(dictionaryData.lookUp(word));
    }

    public DslResult getEntriesPredictive(final String word) {
         return new DslResult(dictionaryData.lookUpPredictive(word));
    }

    private static boolean testLine(final String line) {
        return !line.isEmpty() && !line.startsWith("#");
    }

    public static DslDictionary loadDictionary(final File file) throws IOException {
        if (!file.isFile()) {
            throw new IOException("Target file is not a file.");
        }
        DictionaryDataBuilder<Object> builder = new DictionaryDataBuilder<>();
        StringBuilder word = new StringBuilder();
        StringBuilder trans = new StringBuilder();
        try (FileInputStream fis = new FileInputStream(file)) {
            // Un-gzip if necessary
            InputStream is;
            if (file.getName().endsWith(".dz")) {
                is = new GZIPInputStream(fis, 8192);
            } else {
                is = fis;
            }
            try (BOMInputStream bis = new BOMInputStream(is)) {
                // Detect charset
                Charset charset;
                if (bis.hasBOM()) {
                    charset = StandardCharsets.UTF_8;
                } else {
                    charset = StandardCharsets.UTF_16;
                }
                try (InputStreamReader isr = new InputStreamReader(bis, charset);
                     BufferedReader reader = new BufferedReader(isr)) {
                     reader.lines().filter(DslDictionary::testLine)
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
        return new DslDictionary(data);
    }
}
