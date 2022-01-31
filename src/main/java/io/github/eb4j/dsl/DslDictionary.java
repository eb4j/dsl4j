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
import io.github.eb4j.dsl.data.DslDictionaryProperty;
import io.github.eb4j.dsl.data.DslEntry;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * DslDictionary template method class.
 * DslDictionary class has a loader/factory method.
 *
 * @author Hiroshi Miura
 */
public abstract class DslDictionary {
    protected final DictionaryData<DslEntry> dictionaryData;
    protected final DslDictionaryProperty prop;

    protected DslDictionary(final DictionaryData<DslEntry> dictionaryData, final DslDictionaryProperty prop) {
        this.dictionaryData = dictionaryData;
        this.prop = prop;
    }

    /**
     * Search article with exact match for given word.
     * @param word search word.
     * @return DslResult object
     * @throws IOException when I/O error occurred
     */
    public DslResult lookup(final String word) throws IOException {
        List<Map.Entry<String, String>> result = new ArrayList<>();
        for (Map.Entry<String, DslEntry> en: dictionaryData.lookUp(word)) {
            result.add(new AbstractMap.SimpleImmutableEntry<>(en.getKey(), getArticle(en.getValue())));
        }
        return new DslResult(result);
    }

    /**
     * Search article with prefix search for given word.
     * @param word search word.
     * @return DslResult object
     * @throws IOException when I/O error occurred
     */
    public DslResult lookupPredictive(final String word) throws IOException {
        List<Map.Entry<String, String>> result = new ArrayList<>();
        for (Map.Entry<String, DslEntry> en: dictionaryData.lookUpPredictive(word)) {
            result.add(new AbstractMap.SimpleImmutableEntry<>(en.getKey(), getArticle(en.getValue())));
        }
        return new DslResult(result);
    }

    /**
     * Comcrete implementation should implement a method to get article.
     * @param entry DslEntry to indicate posttion and size of article.
     * @return article string.
     * @throws IOException when I/O error occurred
     */
    abstract String getArticle(DslEntry entry) throws IOException;

    protected String trimArticle(final byte[] buf) {
        String[] tokens = new String(buf, prop.getCharset()).split("\\r?\\n");
        StringBuilder article = new StringBuilder();
        for (String token: tokens) {
            if (token.isEmpty()) { // remove empty line
                continue;
            }
            article.append(token.trim()).append("\n");
        }
        return article.toString();
    }

    public String getDictionaryName() {
        return prop.getDictionaryName();
    }

    public String getIndexLanguage() {
        return prop.getIndexLanguage();
    }

    public String getContentLanguage() {
        return prop.getContentLanguage();
    }

    /**
     * Loader entry point.
     * @param file dictionary file.
     * @return DslDictionary object.
     * @throws IOException raise when I/O error occurred
     */
    public static DslDictionary loadDictionary(@NotNull final File file) throws IOException {
        return loadDictionary(file.toPath());
    }

    /**
     * Loader entry point.
     * @param path dictionary file.
     * @return DslDictionary object.
     * @throws IOException raise when I/O error occurred
     */
    public static DslDictionary loadDictionary(@NotNull final Path path) throws IOException {
        return DslDictionaryLoader.load(path, null, true);
    }

    /**
     * Loader entry point.
     * @param path dictionary file.
     * @param index dictionary index file.
     * @return DslDictionary object.
     * @throws IOException raise when I/O error occurred
     */
    public static DslDictionary loadDictionary(@NotNull final Path path, final Path index) throws IOException {
        return DslDictionaryLoader.load(path, index, false);
    }

    /**
     * Loader entry point.
     * @param path dictionary file.
     * @param index dictionary index file.
     * @param validateIndexAbspath true if validate index by compare with full path of generated one, otherwise false.
     * @return DslDictionary object.
     * @throws IOException raise when I/O error occurred
     */
    public static DslDictionary loadDictionary(@NotNull final Path path, final Path index,
                                               final boolean validateIndexAbspath) throws IOException {
        return DslDictionaryLoader.load(path, index, validateIndexAbspath);
    }
}
