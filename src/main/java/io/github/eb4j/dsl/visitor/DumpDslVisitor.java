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

package io.github.eb4j.dsl.visitor;

import io.github.eb4j.dsl.DslArticle;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Dump visitor adapter.
 */
public class DumpDslVisitor extends DslVisitor<String> {
    protected PrintWriter out;
    protected ByteArrayOutputStream baos = new ByteArrayOutputStream();
    protected Charset charset;

    /**
     * Constructor.
     */
    public DumpDslVisitor() {
        charset = StandardCharsets.UTF_8;
        out = new PrintWriter(new OutputStreamWriter(baos, charset));
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

    /**
     * Return result.
     *
     * @return result.
     */
    @Override
    public String getObject() {
        try {
            return baos.toString(charset.name());
        } catch (UnsupportedEncodingException ignored) {
        }
        return null;
    }
}
