/*
 * DSL4J, a parser library for LingoDSL format.
 * Copyright (C) 2021 Hiroshi Miura.
 * Copyright (C) 1999 Brian Goetz, Quiotix Corporation.
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

import io.github.eb4j.dsl.data.DslEntry;
import io.github.eb4j.dsl.visitor.DslVisitor;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DslResult {

    private final List<Map.Entry<String, String>> result;

    public DslResult(final List<Map.Entry<String, String>> res) {
        result = res;
    }

    public <T> List<Map.Entry<String, T>> getEntries(final DslVisitor<T> filter) {
        List<Map.Entry<String, T>> res = new ArrayList<>();
        for (Map.Entry<String, String> entry: result) {
            try {
                DslParser parser = DslParser.createParser(entry.getValue());
                DslArticle article = parser.DslArticle();
                article.accept(filter);
                res.add(new AbstractMap.SimpleImmutableEntry<>(entry.getKey(), filter.getObject()));
            } catch (ParseException ignored) {
            }
        }
        return res;
    }

}
