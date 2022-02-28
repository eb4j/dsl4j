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
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * DslDictionary template method class.
 * DslDictionary class has a loader/factory method.
 *
 * @author Hiroshi Miura
 */
public abstract class DslDictionary {
    protected final DictionaryData<DslEntry> dictionaryData;
    protected final DslDictionaryProperty prop;
    protected static final String LINE_SEPARATOR = System.getProperty("line.separator");
    protected static final Pattern DELIMITER_PATTERN = Pattern.compile(
            "(\\r\\n|[\\n\\r\\u2028\\u2029\\u0085])+(\\s+)?");

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
            result.add(new AbstractMap.SimpleImmutableEntry<>(getHeadWord(en.getValue()), getArticle(en.getValue())));
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
            result.add(new AbstractMap.SimpleImmutableEntry<>(getHeadWord(en.getValue()), getArticle(en.getValue())));
        }
        return new DslResult(result);
    }

    /**
     * Return article text.
     * @param entry DslEntry to indicate position and size of article.
     * @return article string.
     * @throws IOException when I/O error occurred
     */
    protected String getArticle(final DslEntry entry) throws IOException {
        return trimArticle(getRecord(entry.getOffset(), entry.getSize()));
    }

    /**
     * Return head word.
     * @param entry DslEntry to indicate record.
     * @return article string.
     * @throws IOException
     */
    String getHeadWord(final DslEntry entry) throws IOException {
        return trimArticle(getRecord(entry.getHeaderOffset(), entry.getHeaderSize()));
    }

    abstract String getRecord(long offset, int size) throws IOException;

    protected String trimArticle(final String article) {
        Matcher matcher = DELIMITER_PATTERN.matcher(article);
        return matcher.replaceAll(LINE_SEPARATOR).trim();
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
