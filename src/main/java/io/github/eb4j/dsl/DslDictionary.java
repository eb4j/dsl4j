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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;


public class DslDictionary {
    private final FileInputStream fileInputStream;
    private final DictionaryData<Object> dictionaryData;

    public DslDictionary(final DictionaryData<Object> index, final FileInputStream fileInputStream) {
        dictionaryData = index;
        this.fileInputStream = fileInputStream;
    }

    public List<Map.Entry<String, Object>> getEntries(final String word) {
        return dictionaryData.lookUp(word);
    }

    public List<Map.Entry<String, Object>> getEntriesPredictive(final String word) {
        return dictionaryData.lookUpPredictive(word);
    }

    public String getText(final Long offset) {
        return null;
    }

    public static DslDictionary loadDicitonary(final String path) throws IOException {
        File file = new File(mdxFile);
        if (!file.isFile()) {
            throw new IOException("Target file is not DSL file.");
        }
	return null;
    }

}
