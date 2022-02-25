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

import com.google.protobuf.ByteString;
import io.github.eb4j.dsl.data.DictionaryData;
import io.github.eb4j.dsl.data.DictionaryDataBuilder;
import io.github.eb4j.dsl.data.DslDictionaryProperty;
import io.github.eb4j.dsl.data.DslEntry;
import io.github.eb4j.dsl.impl.EntriesLoaderImpl;
import io.github.eb4j.dsl.index.DslIndex;
import org.apache.commons.io.ByteOrderMark;
import org.apache.commons.io.input.BOMInputStream;
import org.dict.zip.DictZipInputStream;
import org.dict.zip.RandomAccessInputStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


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

    private static final int INDEX_VERSION = 2;

    private DslDictionaryLoader() {
    }

    static DslDictionary load(@NotNull final Path path, @Nullable final Path indexPath,
                              final boolean validateIndexAbsPath) throws IOException {
        // check path
        if (!path.toFile().isFile()) {
            throw new IOException("Target file is not a file.");
        }
        Path filename = path.getFileName();
        if (filename == null) {
            throw new IOException("Error reading target file.");
        }
        boolean isDictzip = filename.toString().endsWith(".dz");
        List<DslIndex.Entry> entries = null;
        DslDictionaryProperty prop = null;
        DslIndex index = getIndexFromFileAndValidate(path, indexPath, validateIndexAbsPath);
        if (index != null) {
            prop = new DslDictionaryProperty(index.getDictionaryName(), index.getIndexLanguage(),
                    index.getContentLanguage(), Charset.forName(index.getCharset()), index.getEol().toByteArray());
            entries = index.getEntriesList();
        }
        // When there is no index or failed to validate
        if (entries == null || entries.isEmpty()) {
            Charset charset = detectCharset(path, isDictzip);
            byte[] eol = detectEol(path, isDictzip, charset);
            Map<String, String> metadata = readMetadata(path, isDictzip, charset);
            try (EntriesLoaderImpl loader = new EntriesLoaderImpl(path, isDictzip, charset, eol)) {
                entries = loader.load();
            }
            prop = new DslDictionaryProperty(
                    metadata.get("name"), metadata.get("index"), metadata.get("content"), charset, eol);
            buildIndexFile(path, indexPath, entries, prop);
        }
        DictionaryData<DslEntry> data = new DictionaryDataBuilder<DslEntry>().build(entries);
        if (isDictzip) {
            return new DslZipDictionary(path, data, prop);
        } else {
            return new DslFileDictionary(path, data, prop);
        }
    }

    private static DslIndex getIndexFromFileAndValidate(final Path path, final Path indexPath,
                                                        final boolean validateAbsolutePath) {
        if (indexPath != null && indexPath.toFile().canRead()) {
            try (InputStream is = new GZIPInputStream(Files.newInputStream(indexPath))) {
                DslIndex index = DslIndex.parseFrom(is);
                if (INDEX_VERSION == index.getIndexVersion()) {
                    long mtime = Files.getLastModifiedTime(path).toMillis();
                    long expectedMTime = index.getFileLastModifiedTime();
                    Path filepath = path.getFileName();
                    String filename;
                    if (filepath != null) {
                        filename = filepath.toString();
                    } else {
                        filename = "";
                    }
                    Path parentpath = path.getParent();
                    String parent;
                    if (parentpath != null) {
                        parent = parentpath.toString();
                    } else {
                        parent = "";
                    }
                    boolean samePath = filename.equals(index.getFilename())
                            && (!validateAbsolutePath || parent.equals(index.getParentPath()));
                    if (samePath && Files.size(path) == index.getFilesize() && mtime == expectedMTime) {
                        return index;
                    }
                }
            } catch (IOException ignored) {
            }
        }
        return null;
    }

    private static void buildIndexFile(@NotNull final Path path, @Nullable final Path indexPath,
                                       @NotNull final List<DslIndex.Entry> entries,
                                       @NotNull final DslDictionaryProperty prop) throws IOException {
        if (indexPath == null) {
            // do nothing when indexPath is not specified.
            return;
        }
        String filename;
        Path filepath = path.getFileName();
        if (filepath != null) {
            filename = filepath.toString();
        } else {
            filename = "";
        }
        String parent;
        Path parentpath = path.getParent();
        if (parentpath != null) {
            parent = parentpath.toString();
        } else {
            parent = "";
        }
        try (OutputStream os = new GZIPOutputStream(Files.newOutputStream(indexPath,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE))) {
            DslIndex index = DslIndex.newBuilder()
                    .setIndexVersion(INDEX_VERSION)
                    .setFilename(filename)
                    .setParentPath(parent)
                    .setFilesize(Files.size(path))
                    .setFileLastModifiedTime(Files.getLastModifiedTime(path).toMillis())
                    .setDictionaryName(prop.getDictionaryName())
                    .setIndexLanguage(prop.getIndexLanguage())
                    .setContentLanguage(prop.getContentLanguage())
                    .setCharset(prop.getCharset().name())
                    .setEol(ByteString.copyFrom(prop.getEol()))
                    .addAllEntries(entries)
                    .build();
            index.writeTo(os);
            os.flush();
        } catch (IOException e) {
            // clean up when error
            Files.deleteIfExists(indexPath);
        }
    }

    @SuppressWarnings("AvoidInlineConditionals")
    private static byte[] detectEol(final Path path, final boolean isDictzip, final Charset charset)
            throws IOException {
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

    @SuppressWarnings("AvoidInlineConditionals")
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
                }
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

    @SuppressWarnings("AvoidInlineConditionals")
    private static Map<String, String> readMetadata(final Path path, final boolean isDictzip, final Charset charset)
            throws IOException {
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

}
