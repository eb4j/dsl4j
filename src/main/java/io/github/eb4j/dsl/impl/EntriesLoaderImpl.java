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

package io.github.eb4j.dsl.impl;

import io.github.eb4j.dsl.index.DslIndex;
import org.dict.zip.DictZipInputStream;
import org.dict.zip.RandomAccessInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EntriesLoaderImpl implements AutoCloseable {

    private final boolean isDictZip;
    private final Charset charset;

    private DictZipInputStream dzis;
    private RandomAccessInputStream rais;

    private final byte[] eol;
    private final byte[] cr;
    private final byte[] lf;
    private final byte[] tab;
    private final byte[] space;
    private final byte[] commentStart;
    private final byte[] sharp;

    public EntriesLoaderImpl(final Path path, final boolean isDictZip, final Charset charset, final byte[] eol)
            throws IOException {
        this.isDictZip = isDictZip;
        this.charset = charset;
        // DictZipInputStream is child of InflateInputStream
        // It shares common ancestor InputStream with RandomAccessInputStream
        // Both class has `position()` method that InputStream don't have.
        if (isDictZip) {
            dzis = new DictZipInputStream(new RandomAccessInputStream(new RandomAccessFile(path.toFile(), "r")));
        } else {
            rais = new RandomAccessInputStream(new RandomAccessFile(path.toFile(), "r"));
        }

        this.eol = Arrays.copyOf(eol, eol.length);
        cr = "\r".getBytes(charset);
        lf = "\n".getBytes(charset);
        tab = "\t".getBytes(charset);
        space = " ".getBytes(charset);
        commentStart = "{{".getBytes(charset);
        sharp = "#".getBytes(charset);
    }

    public void close() throws IOException {
        if (isDictZip) {
            dzis.close();
        } else {
            rais.close();
        }
    }

    public List<DslIndex.Entry> load() throws IOException {
        List<DslIndex.Entry> entries = new ArrayList<>();
        long cardStart;
        long articleStart;
        String headWords;
        cardStart = entryStartSearch();
        while (true) {
            // skip comment
            long next = skipComment();
            // check multiple head words
            long headWordLen = eolSearch();
            if (headWordLen == -1) {
                break;
            }
            while (!isSpaceOrTab()) {
                next = eolSearch();
                if (next == -1) {
                    break;
                }
                headWordLen += next;
            }
            seek(cardStart);
            byte[] headWordBytes = new byte[(int) headWordLen];
            readFully(headWordBytes);
            headWords = new String(headWordBytes, charset).trim();
            long spaces = skipSpaceTabs();
            articleStart = cardStart + headWordLen + spaces;
            seek(articleStart);
            // It can be position() but dictzip may not return correct position
            // articleStart = position();
            long articleLen = cardEndSearch();
            String[] tokens = headWords.split("\\r?\\n");
            for (String token : tokens) {
                entries.add(DslIndex.Entry.newBuilder()
                        .setHeadWord(token)
                        .setOffset(articleStart)
                        .setSize((int) articleLen)
                        .build());
            }
            // reset to new cardStart. previous cardEndSearch() point NEXT to cardStart first character.
            cardStart = articleStart + articleLen;
            if (isEof()) {
                break;
            }
            seek(cardStart);
            int empty;
            if ((empty = skipEmptyLine()) != -1) {
                if (isEof()) {
                    break;
                }
                cardStart += empty;
            } else {
                // EOF detected
                break;
            }
        }
        return entries;
    }

    private void seek(final long pos) throws IOException {
        if (isDictZip) {
            dzis.seek(pos);
        } else {
            rais.seek(pos);
        }
    }

    private boolean isEof() throws IOException {
        if (isDictZip) {
            return dzis.available() <= 0;
        } else {
            return rais.available() <= 0;
        }
    }

    private long position() throws IOException {
        if (isDictZip) {
            return dzis.position();
        } else {
            return rais.position();
        }
    }

    private long skipComment() throws IOException {
        byte[] b = new byte[commentStart.length];
        InputStream is;
        if (isDictZip) {
            is = dzis;
        } else {
            is = rais;
        }
        is.mark(commentStart.length);
        if (is.read(b) > 0) {
            if (Arrays.equals(commentStart, b)) {
                // we should check end of comment, but now we ignore line.
                return eolSearch();
            }
            is.reset();
            return 0;
        }
        // got EOF
        return -1;

    }

    private long entryStartSearch() throws IOException {
        byte[] b = new byte[sharp.length];
        InputStream is;
        if (isDictZip) {
            is = dzis;
        } else {
            is = rais;
        }
        if (eolSearch() == -1) {
            return -1;
        }
        is.mark(sharp.length);
        while (is.read(b) != 0) {
            if (Arrays.equals(sharp, b)) {
                if (eolSearch() == -1) {
                    break;
                }
            } else if (Arrays.equals(cr, b) || Arrays.equals(lf, b)) {
                is.mark(sharp.length);
                continue;
            } else {
                is.reset();
                return position();
            }
            is.mark(sharp.length);
        }
        return -1;
    }

    /**
     * search card-end and return article length.
     *
     * It treat EOF case.
     * @return length of article in byte.
     * @throws IOException when i/o error occurred.
     */
    private long cardEndSearch() throws IOException {
        byte[] b = new byte[tab.length];
        long current = position();
        InputStream is;
        if (isDictZip) {
            is = dzis;
        } else {
            is = rais;
        }
        long len = eolSearch();
        if (len == -1) {
            // EOF detected
            return position() - current;
        }
        boolean eof = true;
        while (is.read(b) != 0) {
            if (!Arrays.equals(tab, b) && !Arrays.equals(space, b)) {
                eof = false;
                break;
            }
            len = eolSearch();
            if (len == -1) {
                // EOF detected.
                break;
            }
        }
        if (eof) {
            return position() - current;
        } else {
            return position() - current - tab.length;
        }
    }

    private long skipSpaceTabs() throws IOException {
        long readBytes = 0;
        InputStream is;
        if (isDictZip) {
            is = dzis;
        } else {
            is = rais;
        }
        is.mark(tab.length);
        while (isSpaceOrTab()) {
            readBytes += tab.length;
            is.mark(tab.length);
        }
        is.reset();
        return readBytes;
    }

    private void readFully(final byte[] buffer) throws IOException {
        if (isDictZip) {
            dzis.readFully(buffer);
        } else {
            rais.readFully(buffer);
        }
    }

    private boolean isSpaceOrTab() throws IOException {
        byte[] b = new byte[tab.length];
        InputStream is;
        if (isDictZip) {
            is = dzis;
        } else {
            is = rais;
        }
        if (is.read(b) != 0) {
            return Arrays.equals(tab, b) || Arrays.equals(space, b);
        }
        return false;
    }

    private int skipEmptyLine() throws IOException {
        int readByte = 0;
        byte[] b = new byte[cr.length];
        InputStream is;
        if (isDictZip) {
            is = dzis;
        } else {
            is = rais;
        }
        is.mark(cr.length);
        while (is.read(b) > 0) {
            if (Arrays.equals(cr, b)) {
                readByte += cr.length;
                if (is.read(b) != 0 && Arrays.equals(lf, b)) {
                    readByte += lf.length;
                } else {
                    // CR without LF
                    throw new IOException("CR without LF line termination.");
                }
            } else {
                // character other than CR found
                // check LF?
                if (!Arrays.equals(lf, b)) {
                    is.reset();
                    return readByte;
                } else {
                    readByte += lf.length;
                }
            }
            is.mark(cr.length);
        }
        // end of file without CRLF
        return -1;
    }

    /**
     * Lazy searcher for EOL terminator.
     *
     * DSL file can take 4 variations of line end terminator.
     * UTF-16LE CR+LF:  0x0d 0x00 0x0a 0x00
     * UTF-16LE LF: 0x0a 0x00
     * UTF-8 or ANSI LF: 0x0a
     * UTF-8 or ANSI CR+LF: 0x0d 0x0a
     * @return -1 when EoF, otherwise a distance to eol from current position.
     * @throws IOException when i/o error occurred.
     */
    private long eolSearch() throws IOException {
        int b;
        InputStream stream;
        boolean isUTF16 = StandardCharsets.UTF_16LE.equals(charset);
        long current;
        if (isDictZip) {
            stream = dzis;
            current = dzis.position();
        } else {
            stream = rais;
            current = rais.position();
        }
        while ((b = stream.read()) != -1) {
            if ((byte) b != 0x0a) {
                continue;
            }
            if (!isUTF16) {
                // LF found when UTF-8 and ANSI charsets
                if (isDictZip) {
                    return dzis.position() - current;
                } else {
                    return rais.position() - current;
                }
            }
            // check second byte
            if ((b = stream.read()) == -1) {
                // eof detected after 0x0a found in UTF-16 case.
                return -1;
            }
            if (b != 0x00) {
                // it is other than LF, just lower byte is 0x0a
                continue;
            }
            // Found LF in UTF-16LE
            if (isDictZip) {
                return dzis.position() - current;
            } else {
                return rais.position() - current;
            }
        }
        // no dice, eof found.
        return -1;
    }
}
