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

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;

public class DslDumper extends DslVisitor {
    protected PrintWriter out;

    /** Constructor. */
    public DslDumper(OutputStream os) {
        out = new PrintWriter(os);
    }

    /** Constructor. */
    public DslDumper(OutputStream os, Charset encoding) {
        out = new PrintWriter(new OutputStreamWriter(os, encoding));
    }

    public void finish() {
        out.flush();
    }

    /**
     * Visit a tag
     *
     * @param tag
     */
    @Override
    public void visit(DslArticle.Tag tag) {
        out.print(tag);
    }

    /**
     * Visit a text
     *
     * @param t
     */
    @Override
    public void visit(DslArticle.Text t) {
        out.print(t);
    }

    /**
     * Visit an Attribute
     *
     * @param a
     */
    @Override
    public void visit(DslArticle.Attribute a) {
        out.print(a);
    }

    /**
     * Visit a NewLine
     *
     * @param n
     */
    @Override
    public void visit(DslArticle.Newline n) {
        out.println();
    }

    /**
     * Visit an EndTag
     *
     * @param endTag
     */
    @Override
    public void visit(DslArticle.EndTag endTag) {
        out.print(endTag);
    }
}
