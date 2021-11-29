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

package io.github.eb4j.dsl.visitor;

import io.github.eb4j.dsl.DslArticle;

/**
 * Simple HTML filter for LingvoDSL parser.
 * <p>
 *     A sample of visitor converting to HTML.
 * </p>
 * @author Hiroshi Miura
 */
public class DslHtmlAdapter extends AbstractDslVisitor<String> {

    private StringBuilder sb;

    /**
     * Constructor.
     */
    public DslHtmlAdapter() {
    }

    @Override
    public void start() {
        sb = new StringBuilder();
    }

    @Override
    public void finish() {
    }

    /**
     * Visit a tag.
     *
     * @param tag to visit.
     */
    @Override
    public void visit(final DslArticle.Tag tag) {
        if (tag.isTagName("b")) {
            sb.append("<strong>");
        } else if (tag.isTagName("u")) {
            sb.append("<span style='text-decoration:underline'>");
        } else if (tag.isTagName("i")) {
            sb.append("<span style='font-style: italic'>");
        } else if (tag.isTagName("t")) {
            sb.append("<span>");
        } else if (tag.isTagName("sup")) {
            sb.append("<sup>");
        } else if (tag.isTagName("sub")) {
            sb.append("<sub>");
        } else if (tag.isTagName("m")) {
            sb.append("<p style=\"text-indent: 30px\">");
        } else if (tag.isTagName("m1")) {
            sb.append("<p style=\"text-indent: 30px\">");
        } else if (tag.isTagName("m2")) {
            sb.append("<p style=\"text-indent: 60px\">");
        } else if (tag.isTagName("m3")) {
            sb.append("<p style=\"text-indent: 90px\">");
        } else if (tag.isTagName("m4")) {
            sb.append("<p style=\"text-indent: 90px\">");
        } else if (tag.isTagName("m5")) {
            sb.append("<p style=\"text-indent: 90px\">");
        } else if (tag.isTagName("m6")) {
            sb.append("<p style=\"text-indent: 90px\">");
        } else if (tag.isTagName("m7")) {
            sb.append("<p style=\"text-indent: 90px\">");
        } else if (tag.isTagName("m8")) {
            sb.append("<p style=\"text-indent: 90px\">");
        } else if (tag.isTagName("m9")) {
            sb.append("<p style=\"text-indent: 90px\">");
        } else if (tag.isTagName("c")) {
            if (tag.hasAttribute()) {
                sb.append("<span style=\"color: ").append(tag.getAttribute().getValue()).append("\">");
            } else {
                sb.append("<span style=\"color: green\">");
            }
        } else if (tag.isTagName("url")) {
            sb.append("<a href=\"");
        }
    }

    /**
     * Visit an EndTag.
     *
     * @param endTag to visit.
     */
    @Override
    public void visit(final DslArticle.EndTag endTag) {
        if (endTag.isTagName("b")) {
            sb.append("</strong>");
        } else if (endTag.isTagName("u")) {
            sb.append("</span>");
        } else if (endTag.isTagName("i")) {
            sb.append("</span>");
        } else if (endTag.isTagName("t")) {
            sb.append("&nbsp;</span>");
        } else if (endTag.isTagName("sup")) {
            sb.append("</sup>");
        } else if (endTag.isTagName("sub")) {
            sb.append("</sub>");
        } else if (endTag.isTagName("m")) {
            sb.append("</p>");
        } else if (endTag.isTagName("c")) {
            sb.append("</span>");
        } else if (endTag.isTagName("url")) {
            sb.append("\">LINK</a>");
        }
    }

    /**
     * Return result.
     *
     * @return result.
     */
    @Override
    public String getObject() {
        if (sb == null) {
            return "";
        }
        return sb.toString();
    }

    /**
     * Visit a text.
     *
     * @param t Text object to process.
     */
    @Override
    public void visit(final DslArticle.Text t) {
        sb.append(t);
    }

    /**
     * Visit an Attribute.
     *
     * @param a Attribute object to visit.
     */
    @Override
    public void visit(final DslArticle.Attribute a) {
    }

    /**
     * Visit a NewLine.
     *
     * @param n NewLine object to visit.
     */
    @Override
    public void visit(final DslArticle.Newline n) {
        sb.append("\n");
    }
}
