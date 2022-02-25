/*
 * DSL4J, a parser library for DSL format.
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

package io.github.eb4j.dsl.data;

import io.github.eb4j.dsl.index.DslIndex;
import org.trie4j.MapTrie;
import org.trie4j.patricia.MapPatriciaTrie;

import java.util.List;

public final class DictionaryDataBuilder<T> {

    /**
     * Builder factory for POJO class DictionaryData.
     */
    public DictionaryDataBuilder() {
    }

    /**
     * build DictionaryData POJO.
     * @param entries List of ProtoBuf defined entry
     * @return DictionaryData immutable object.
     */
    public DictionaryData<T> build(final List<DslIndex.Entry> entries) {
        MapTrie<Object> mapPatriciaTrie = new MapPatriciaTrie<>();
        for (DslIndex.Entry en: entries) {
            doAdd(mapPatriciaTrie, en.getHeadWord(), en.getOffset(), en.getSize(), en.getHeaderOffset(), en.getHeaderSize());
            String lowerKey = en.getHeadWord().toLowerCase();
            if (!en.getHeadWord().equals(lowerKey)) {
                doAdd(mapPatriciaTrie, lowerKey, en.getOffset(), en.getSize(), en.getHeaderOffset(), en.getHeaderSize());
            }
        }
        return new DictionaryData<>(mapPatriciaTrie);
    }

    /**
     * Do the actual storing of the value. Most values are going to be singular,
     * but dictionaries may store multiple definitions for the same key, so in
     * that case we store the values in an array.
     *
     * @param key
     */
    private void doAdd(final MapTrie<Object> mapPatriciaTrie, final String key, final long offset, final int size,
                       final long headerOffiset, final int headerSize) {
        Object stored = mapPatriciaTrie.get(key);
        if (stored == null) {
            mapPatriciaTrie.insert(key, new DslEntry(headerOffiset, headerSize, offset, size));
        } else {
            if (stored instanceof Object[]) {
                stored = extendArray((Object[]) stored, new DslEntry(headerOffiset, headerSize, offset, size));
            } else {
                stored = new Object[] {stored, new DslEntry(headerOffiset, headerSize, offset, size)};
            }
            mapPatriciaTrie.put(key, stored);
        }
    }

    /**
     * Return the given array with the given value appended to it.
     *
     * @param array
     * @param value
     * @return
     */
    Object[] extendArray(final Object[] array, final Object value) {
        Object[] newArray = new Object[array.length + 1];
        System.arraycopy(array, 0, newArray, 0, array.length);
        newArray[newArray.length - 1] = value;
        return newArray;
    }

}
