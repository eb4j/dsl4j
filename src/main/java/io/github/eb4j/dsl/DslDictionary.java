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
import io.github.eb4j.dsl.data.DslEntry;
import org.apache.commons.io.ByteOrderMark;
import org.apache.commons.io.input.BOMInputStream;
import org.dict.zip.DictZipInputStream;
import org.dict.zip.RandomAccessInputStream;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;


public abstract class DslDictionary {
    private static final String[] PATTERNS = {"name", "index", "content", "codepage", "include"};
    private static final Pattern METAPATTERN = Pattern.compile(
                    "^#(NAME\\s(?<name>.+?)"
                    + "|INDEX_LANGUAGE\\s(?<index>.+?)"
                    + "|CONTENTS_LANGUAGE\\s(?<content>.+?)"
                    + "|SOURCE_CODE_PAGE\\s(?<codepage>.+?))"
                    + "|INCLUDE\\s(?<include>.+?)$");
    private static final String[] ALLOWED_CODE_PAGE = {"EasternEuropean", "Cyrillic", "Latin", "Greek", "Turkish"};
    protected final DictionaryData<DslEntry> dictionaryData;
    protected final DslDictionaryProperty prop;

    public DslDictionary(final DictionaryData<DslEntry> dictionaryData, final DslDictionaryProperty prop) {
        this.dictionaryData = dictionaryData;
        this.prop = prop;
    }

    public DslResult lookup(final String word) throws IOException {
        List<Map.Entry<String, String>> result = new ArrayList<>();
        for (Map.Entry<String, DslEntry> en: dictionaryData.lookUp(word)) {
            result.add(new AbstractMap.SimpleImmutableEntry<>(en.getKey(), getArticle(en.getValue())));
        }
        return new DslResult(result);
    }

    public DslResult lookupPredictive(final String word) throws IOException {
        List<Map.Entry<String, String>> result = new ArrayList<>();
        for (Map.Entry<String, DslEntry> en: dictionaryData.lookUpPredictive(word)) {
            result.add(new AbstractMap.SimpleImmutableEntry<>(en.getKey(), getArticle(en.getValue())));
        }
        return new DslResult(result);
    }

    abstract String getArticle(DslEntry entry) throws IOException;

    public String getDictionaryName() {
        return prop.getDictionaryName();
    }

    public String getIndexLanguage() {
        return prop.getIndexLanguage();
    }

    public String getContentLanguage() {
        return prop.getContentLanguage();
    }

    public static DslDictionary loadDictionary(final File file) throws IOException {
        return loadDictionary(file.toPath());
    }

    public static DslDictionary loadDictionary(final Path path) throws IOException {
        final Map<String, String> metadata = new HashMap<>();
        if (!path.toFile().isFile()) {
            throw new IOException("Target file is not a file.");
        }
        boolean isDictzip = false;
        // Read header
        Charset charset;
        try (InputStream rais = Files.newInputStream(path)) {
            // Un-gzip if necessary
            InputStream is;
            if (path.getFileName().endsWith(".dz")) {
                is = new GZIPInputStream(rais, 8192);
                isDictzip = true;
            } else {
                is = rais;
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
                                } else {
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
        //
        DslDictionaryProperty prop = new DslDictionaryProperty(metadata.get("name"), metadata.get("index"),
                metadata.get("content"), charset);
        // Reopen dictionary and build dictionary index
        DictionaryDataBuilder<DslEntry> builder = new DictionaryDataBuilder<>();
        InputStream is;
        RandomAccessFile ras = new RandomAccessFile(path.toFile(), "r");
        if (isDictzip) {
            is = new DictZipInputStream(new RandomAccessInputStream(ras));
        } else {
            is = new RandomAccessInputStream(ras);
        }
        if (!is.markSupported()) {
            throw new IOException();
        }
        // fixme. accept other than UTF16
        byte[] crlf = "\r\n".getBytes(charset);
        byte[] delimiter = "\r\n\r\n".getBytes(charset);
        StreamSearcher crlfSearcher = new StreamSearcher(crlf);
        StreamSearcher cardEndSearcher = new StreamSearcher(delimiter);
        try {
            long cardStart;
            long articleStart;
            String headWords;
            // search delimiter to skip header
            cardStart = cardEndSearcher.search(is);
            while (true) {
                cardStart += skipEmptyLine(is, charset);
                is.mark(4096);
                long next = crlfSearcher.search(is);
                if (next == -1) {
                    throw new IOException();
                }
                while (!isSpaceOrTab(is, charset)) {
                    next += crlfSearcher.search(is);
                }
                articleStart = cardStart + next;
                is.reset();
                byte[] headWordBytes = new byte[(int) next];
                if (is.read(headWordBytes) == 0) {
                    throw new IOException();
                }
                headWords = chompLine(new String(headWordBytes, charset));
                is.mark(4096);
                long pos = cardEndSearcher.search(is);
                if (pos == -1) {
                    is.reset();
                    // last article
                    String[] tokens = headWords.split("\\r?\\n");
                    for (String token: tokens) {
                        builder.add(token, new DslEntry(articleStart, (is.available())));
                    }
                    break;
                }
                String[] tokens = headWords.split("\\r?\\n");
                for (String token: tokens) {
                    builder.add(token, new DslEntry(articleStart, (int) pos - crlf.length));
                }
                // increment to next card start
                cardStart = articleStart + pos;
            }
        } finally {
            is.close();
        }
        DictionaryData<DslEntry> data = builder.build();
        if (isDictzip) {
            return new DslZipDictionary(path, data, prop);
        } else {
            return new DslFileDictionary(path, data, prop);
        }
    }

    protected static String chompLine(final String line) {
        return line.substring(0, line.lastIndexOf("\r"));
    }

    protected static boolean isSpaceOrTab(final InputStream is, final Charset charset) throws IOException {
        byte[] tab = "\t".getBytes(charset);
        byte[] space = " ".getBytes(charset);
        byte[] b = new byte[tab.length];
        if (is.read(b) != 0) {
            return Arrays.equals(tab, b) || Arrays.equals(space, b);
        }
        // end of file found
        return false;
    }

    protected static int skipEmptyLine(final InputStream is, final Charset charset) throws IOException {
        int readByte = 0;
        byte[] cr = "\r".getBytes(charset);
        byte[] lf = "\n".getBytes(charset);
        byte[] b = new byte[cr.length];
        is.mark(cr.length);
        while (is.read(b) != 0) {
            if (!Arrays.equals(cr, b)) {
                // character other than CR found
                is.reset();
                return readByte;
            }
            readByte += cr.length;
            if (is.read(b) != 0 && Arrays.equals(lf, b)) {
                readByte += lf.length;
            } else {
                // CR without LF
                throw new IOException();
            }
            is.mark(2);
        }
        // end of file without CRLF
        return readByte;
    }
}
