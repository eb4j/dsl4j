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

import java.io.File;
import java.io.IOException;

/**
 * Simple HTML filter for LingvoDSL parser.
 * <p>
 *     A sample of visitor converting to HTML.
 * </p>
 * @author Hiroshi Miura
 */
public class HtmlDslVisitor extends DslVisitor<String> {

    private static final String[] IMAGE_EXTS = new String[] {
            ".png", ".jpg", ".PNG", ".JPG", ".jpeg"
            // , ".tif", ".TIF", ".BMP", ".bmp", ".tiff", ".TIFF"
    };

    private StringBuilder sb;
    private boolean specialTag;
    private String current;
    private final File basePath;

    /**
     * Constructor.
     */
    public HtmlDslVisitor() {
        basePath = new File(".");
        specialTag = false;
    }

    /**
     * Constructor with media path.
     * @param dirPath media base path.
     * @throws IOException when given directory not found.
     */
    public HtmlDslVisitor(final String dirPath) throws IOException {
        File dir = new File(dirPath);
        if (!dir.isDirectory()) {
            throw new IOException("Directory not found!");
        }
        basePath = dir;
        specialTag = false;
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
            sb.append("<span class=\"term\">");
        } else if (tag.isTagName("sup")) {
            sb.append("<sup>");
        } else if (tag.isTagName("sub")) {
            sb.append("<sub>");
        } else if (tag.isTagName("br")) {
            sb.append("<br/>");
        } else if (tag.isTagName("m")) {
            sb.append("<p>");
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
        } else if (tag.isTagName("'")) {
            sb.append("<span style=\"color: red\">");
        } else if (tag.isTagName("s") || tag.isTagName("video")) {
            specialTag = true;
        }
    }

    private String getMediaUrl() {
        return new File(basePath, current).toURI().toString();
    }

    private boolean isMediaImage() {
        for (String ext: IMAGE_EXTS) {
            if (current.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Visit an EndTag.
     *
     * @param endTag to visit.
     */
    @Override
    public void visit(final DslArticle.EndTag endTag) {
        if (specialTag) {
            if (current == null) {
                return;
            }
            if (endTag.isTagName("video")) {
                sb.append("<a href=\"").append(getMediaUrl()).append("\">").append(current).append("</a>");
            } else if (endTag.isTagName("s")) {
                if (isMediaImage()) {
                    sb.append("<img src=\"").append(getMediaUrl()).append("\" />");
                } else {  // sound and unknown files
                    sb.append("<a href=\"").append(getMediaUrl()).append("\" >").append(current).append("</a>");
                }
            }
            specialTag = false;
            current = null;
        }
        if (endTag.isTagName("b")) {
            sb.append("</strong>");
        } else if (endTag.isTagName("c") || endTag.isTagName("'") || endTag.isTagName("u") || endTag.isTagName("i")) {
            sb.append("</span>");
        } else if (endTag.isTagName("t")) {
            sb.append("&nbsp;</span>");
        } else if (endTag.isTagName("sup")) {
            sb.append("</sup>");
        } else if (endTag.isTagName("sub")) {
            sb.append("</sub>");
        } else if (endTag.isTagName("m")) {
            sb.append("</p>");
        } else if (endTag.isTagName("url")) {
            sb.append("<a href=\"").append(current).append("\">").append(current).append("</a>");
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
        current = t.getText();
        if (!specialTag) {
            sb.append(t);
        }
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
