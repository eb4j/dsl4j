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

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class DslDumper extends DslVisitor {
    protected PrintWriter out;

    /**
     * Constructor.
     * @param os OutputStream to dump.
     */
    public DslDumper(final OutputStream os) {
        this(os, StandardCharsets.UTF_8);
    }

    /**
     * Constructor.
     * @param os OutputStream to dump.
     * @param encoding character set when write to os.
     */
    public DslDumper(final OutputStream os, final Charset encoding) {
        out = new PrintWriter(new OutputStreamWriter(os, encoding));
    }

    /**
     * Start dumping.
     */
    @Override
    public void start() {
        // Do nothing.
    }

    /**
     * Finish dumping.
     */
    @Override
    public void finish() {
        out.flush();
    }

    /**
     * Visit a Tag.
     *
     * @param tag to visit.
     */
    @Override
    public void visit(final DslArticle.Tag tag) {
        out.print(tag);
    }

    /**
     * Visit a Text.
     *
     * @param t Text object to visit.
     */
    @Override
    public void visit(final DslArticle.Text t) {
        String result = t.getText();
        if (result.equals("[")) {
            result = "\\[";
        }
        if (result.equals("]")) {
            result = "\\]";
        }
        out.print(result);
    }

    /**
     * Visit an Attribute.
     *
     * @param a Attribute object to visit.
     */
    @Override
    public void visit(final DslArticle.Attribute a) {
        out.print(a);
    }

    /**
     * Visit a NewLine.
     *
     * @param n NewLine object to visit.
     */
    @Override
    public void visit(final DslArticle.Newline n) {
        out.println();
    }

    /**
     * Visit an EndTag.
     *
     * @param endTag to visit.
     */
    @Override
    public void visit(final DslArticle.EndTag endTag) {
        out.print(endTag);
    }
}
