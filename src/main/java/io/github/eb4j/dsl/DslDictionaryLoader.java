/*
 * DSL4J, a parser library for LingoDSL format.
 * Copyright (C) 2021,2022 Hiroshi Miura.
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
import io.github.eb4j.dsl.data.DslDictionaryProperty;
import io.github.eb4j.dsl.data.DslEntry;
import org.apache.commons.io.ByteOrderMark;
import org.apache.commons.io.input.BOMInputStream;
import org.dict.zip.DictZipInputStream;
import org.dict.zip.RandomAccessInputStream;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Loader class for DSL dictionary.
 */
final class DslDictionaryLoader {
    private static final String[] PATTERNS = {"name", "index", "content", "codepage", "include"};
    private static final Pattern METAPATTERN = Pattern.compile(
                    "^(\uFEFF)?#(NAME\\s(?<name>.+?)"
                    + "|INDEX_LANGUAGE\\s(?<index>.+?)"
                    + "|CONTENTS_LANGUAGE\\s(?<content>.+?)"
                    + "|SOURCE_CODE_PAGE\\s(?<codepage>.+?))"
                    + "|INCLUDE\\s(?<include>.+?)$");
    private static final String[] ALLOWED_CODE_PAGE = {"EasternEuropean", "Cyrillic", "Latin", "Greek", "Turkish"};

    private DslDictionaryLoader() {
    }

    @SuppressWarnings("AvoidInlineConditionals")
    static DslDictionary load(@NotNull final Path path) throws IOException {
        // check path
        if (!path.toFile().isFile()) {
            throw new IOException("Target file is not a file.");
        }
        Path filename = path.getFileName();
        if (filename == null) {
            throw new IOException("Error reading target file.");
        }
        boolean isDictzip = filename.toString().endsWith(".dz");
        Charset charset = detectCharset(path, isDictzip);
        byte[] eol = detectEol(path, isDictzip, charset);
        Map<String, String> metadata = readMetadata(path, isDictzip, charset);
        DslDictionaryProperty prop = new DslDictionaryProperty(
                metadata.get("name"), metadata.get("index"), metadata.get("content"), charset, eol);
        // prepare creation of index
        byte[] delimiter = Arrays.copyOf(eol, eol.length * 2);
        System.arraycopy(eol, 0, delimiter, eol.length, eol.length);
        StreamSearcher eolSearcher = new StreamSearcher(eol);
        StreamSearcher cardEndSearcher = new StreamSearcher(delimiter);
        DictionaryDataBuilder<DslEntry> builder = new DictionaryDataBuilder<>();
        // build dictionary index
        try (InputStream is = isDictzip ? new DictZipInputStream(
                new RandomAccessInputStream(new RandomAccessFile(path.toFile(), "r"))) :
                new RandomAccessInputStream(new RandomAccessFile(path.toFile(), "r"))) {
            long cardStart;
            long articleStart;
            String headWords;
            // search delimiter to skip header
            cardStart = cardEndSearcher.search(is);
            while (true) {
                cardStart += skipEmptyLine(is, charset);
                is.mark(4096);
                long next = eolSearcher.search(is);
                if (next == -1) {
                    break;
                }
                while (!isSpaceOrTab(is, charset)) {
                    next += eolSearcher.search(is);
                }
                articleStart = cardStart + next;
                is.reset();
                byte[] headWordBytes = new byte[(int) next];
                if (is.read(headWordBytes) == 0) {
                    throw new IOException();
                }
                headWords = new String(headWordBytes, charset).trim();
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
                    builder.add(token, new DslEntry(articleStart, (int) pos - eol.length));
                }
                // increment to next card start
                cardStart = articleStart + pos;
            }
        }
        DictionaryData<DslEntry> data = builder.build();
        if (isDictzip) {
            return new DslZipDictionary(path, data, prop);
        } else {
            return new DslFileDictionary(path, data, prop);
        }
    }

    @SuppressWarnings("AvoidInlineConditionals")
    private static byte[] detectEol(final Path path, final boolean isDictzip, final Charset charset) throws IOException {
        byte[] eol;
        try (InputStream bis = isDictzip ? new DictZipInputStream(
                    new RandomAccessInputStream(new RandomAccessFile(path.toFile(), "r"))) :
                new RandomAccessInputStream(new RandomAccessFile(path.toFile(), "r"))) {
            // default EoL terminator is CR+LF
            eol = "\r\n".getBytes(charset);
            // detect end of line delimiter
            int c;
            try (InputStreamReader isr = new InputStreamReader(bis, charset);
                 BufferedReader reader = new BufferedReader(isr)) {
                while ((c = reader.read()) != -1) {
                    if (c == '\r') {
                        eol = "\r\n".getBytes(charset);
                        break;
                    } else if (c == '\n') {
                        eol = "\n".getBytes(charset);
                        break;
                    }
                }
            }
        }
        return eol;
    }

    private static Charset detectCharset(final Path path, final boolean isDictzip) throws IOException {
        Map<String, String> metadata;
        Charset charset;
        try (BOMInputStream bis = isDictzip ? new BOMInputStream(new DictZipInputStream(
                new RandomAccessInputStream(new RandomAccessFile(path.toFile(), "r"))),
                false, ByteOrderMark.UTF_8, ByteOrderMark.UTF_16LE) :
                new BOMInputStream(new RandomAccessInputStream(new RandomAccessFile(path.toFile(), "r")),
                        false, ByteOrderMark.UTF_8, ByteOrderMark.UTF_16LE)) {
            // Detect codepage and charset
            if (!bis.hasBOM()) {
                byte[] buf = new byte[4];
                if (bis.read(buf, 0, 4) == -1) {
                    throw new IOException("Unexpected end of file.");
                };
                if (buf[1] == '\0') {
                    charset = StandardCharsets.UTF_16LE;
                } else {
                    // Temporary set ANSI charset
                    charset = StandardCharsets.ISO_8859_1;
                }
            } else if (bis.hasBOM(ByteOrderMark.UTF_16LE)) {
                charset = StandardCharsets.UTF_16LE;
            } else {
                charset = StandardCharsets.UTF_8;
            }
        }
        if (charset == StandardCharsets.ISO_8859_1) {
            metadata = readMetadata(path, isDictzip, charset);
            if (metadata.containsKey("codepage")) {
                String codepageName = metadata.get("codepage");
                for (int i = 0; i < ALLOWED_CODE_PAGE.length; i++) {
                    String name = ALLOWED_CODE_PAGE[i];
                    if (name.equals(codepageName)) {
                        charset = Charset.forName(String.format("Cp%4d", 1250 + i));
                        break;
                    }
                }
            }
            if (charset == StandardCharsets.ISO_8859_1) {
                // When codepage is not set but not UTF-16LE, then assumes UTF-8
                charset = StandardCharsets.UTF_8;
            }
        }
        return charset;
    }

    private static Map<String, String> readMetadata(final Path path, final boolean isDictzip, Charset charset) throws IOException {
        final Map<String, String> metadata = new HashMap<>();
        try (InputStream bis = isDictzip ? new DictZipInputStream(
                new RandomAccessInputStream(new RandomAccessFile(path.toFile(), "r"))) :
                new RandomAccessInputStream(new RandomAccessFile(path.toFile(), "r"));
             InputStreamReader isr = new InputStreamReader(bis, charset);
             BufferedReader reader = new BufferedReader(isr)) {
            String l;
            while ((l = reader.readLine()) != null && !l.isEmpty()) {
                Matcher m = METAPATTERN.matcher(l);
                if (m.matches()) {
                    for (String pattern : PATTERNS) {
                        String s = m.group(pattern);
                        if (s != null) {
                            if (s.startsWith("\"") && s.endsWith("\"")) {
                                metadata.put(pattern, s.substring(1, s.length() - 1));
                            } else {
                                metadata.put(pattern, m.group(pattern));
                            }
                            break;
                        }
                    }
                }
            }
        }
        return metadata;
    }

    private static boolean isSpaceOrTab(final InputStream is, final Charset charset) throws IOException {
        byte[] tab = "\t".getBytes(charset);
        byte[] space = " ".getBytes(charset);
        byte[] b = new byte[tab.length];
        if (is.read(b) != 0) {
            return Arrays.equals(tab, b) || Arrays.equals(space, b);
        }
        // end of file found
        return false;
    }

    private static int skipEmptyLine(final InputStream is, final Charset charset) throws IOException {
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
