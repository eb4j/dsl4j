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
package io.github.eb4j.dsl;

import io.github.eb4j.dsl.data.DictionaryData;
import io.github.eb4j.dsl.data.DslDictionaryProperty;
import io.github.eb4j.dsl.data.DslEntry;
import org.dict.zip.RandomAccessInputStream;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;


public class DslFileDictionary extends DslDictionary {

    private final RandomAccessInputStream ras;

    public DslFileDictionary(final Path path, final DictionaryData<DslEntry> dictionaryData,
                             final DslDictionaryProperty prop) throws IOException {
        super(dictionaryData, prop);
        ras = new RandomAccessInputStream(new RandomAccessFile(path.toFile(), "r"));
    }

    @Override
    protected String getArticle(final DslEntry entry) throws IOException {
        byte[] buf = new byte[entry.getSize()];
        ras.seek(entry.getOffset());
        ras.readFully(buf);
        return trimArticle(buf);
    }
}
