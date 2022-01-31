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
import io.github.eb4j.dsl.data.LanguageCode;
import io.github.eb4j.dsl.data.LanguageName;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple HTML filter for LingvoDSL parser.
 * <p>
 *     A sample of visitor converting to HTML.
 * </p>
 * @author Hiroshi Miura
 */
public class HtmlDslVisitor extends DslVisitor<String> {

    private static final String[] IMAGE_EXTS = new String[] {"png", "jpg", "PNG", "JPG", "jpeg"};
    private static final Map<String, String> TAGMAP = new HashMap<>();
    private static final Map<String, String> ENDTAGMAP = new HashMap<>();
    private final LanguageCode langCode = new LanguageCode();
    private final LanguageName langName = new LanguageName();

    private final File basePath;

    private StringBuilder sb;
    private boolean specialTag;
    private String current;

    /**
     * Default constructor.
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
        if (TAGMAP.containsKey(tag.getTagName())) {
            sb.append(TAGMAP.get(tag.getTagName()));
            return;
        }
        // Handle URL and media tags
        if (tag.isTagName("url") || tag.isTagName("s") || tag.isTagName("video")) {
            specialTag = true;
            return;
        }
        // Handle color tags
        if (tag.isTagName("c")) {
            if (tag.hasAttribute()) {
                sb.append("<span style=\"color: ").append(tag.getAttribute().getValue()).append("\">");
            } else {
                sb.append("<span style=\"color: green\">");
            }
        } else if (tag.isTagName("'")) {
            sb.append("<span style=\"color: red\">");
        } else if (tag.isTagName("lang")) {
            if (tag.hasAttribute() && tag.getAttribute().getKey().equals("id")) {
                int i = Integer.parseInt(tag.getAttribute().getValue());
                if (langCode.containsKey(i)) {
                    sb.append("<span class=\"lang_").append(langCode.get(i)).append("\">");
                    return;
                }
            } else if (tag.hasAttribute() && tag.getAttribute().getKey().equals("id")) {
                if (langName.containsKey(tag.getAttribute().getValue())) {
                    sb.append("<span class=\"lang_").append(langName.get(tag.getAttribute().getValue())).append("\">");
                    return;
                }
            }
            sb.append("<span>");
        }
    }

    private String getMediaUrl() {
        return new File(basePath, current).toURI().toString();
    }

    private boolean isMediaImage() {
        String ext = FilenameUtils.getExtension(current);
        for (String e : IMAGE_EXTS) {
            if (e.equals(ext)) {
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
                // ignore tag when no content such as [url][/url], [s][/s]
                return;
            }
            if (endTag.isTagName("url")) {
                sb.append("<a href=\"").append(current).append("\">").append(current).append("</a>");
            } else if (endTag.isTagName("video") || (endTag.isTagName("s") && !isMediaImage())) {
                // hyperlink when video or sound
                sb.append("<a href=\"").append(getMediaUrl()).append("\">").append(current).append("</a>");
            } else if (endTag.isTagName("s")) {
                // img tag when image file
                sb.append("<img src=\"").append(getMediaUrl()).append("\" />");
            }
            specialTag = false;
            current = null;
        }
        if (ENDTAGMAP.containsKey(endTag.getTagName())) {
            sb.append(ENDTAGMAP.get(endTag.getTagName()));
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

    static {
        TAGMAP.put("b", "<strong>");
        ENDTAGMAP.put("b", "</strong>");
        TAGMAP.put("br", "<br/>");
        TAGMAP.put("i", "<span style='font-style: italic'>");
        ENDTAGMAP.put("i", "</span>");
        TAGMAP.put("t", "<span class=\"term\">");
        ENDTAGMAP.put("t", "&nbsp;</span>");
        TAGMAP.put("u", "<span style='text-decoration:underline'>");
        ENDTAGMAP.put("u", "</span>");
        TAGMAP.put("sup", "<sup>");
        ENDTAGMAP.put("sup", "</sup>");
        TAGMAP.put("sub", "</sub>");
        ENDTAGMAP.put("sub", "</sub>");
        ENDTAGMAP.put("c", "</span>");
        ENDTAGMAP.put("'", "</span>");
        TAGMAP.put("m", "<p>");
        TAGMAP.put("m1", "<p style=\"text-indent: 30px\">");
        TAGMAP.put("m2", "<p style=\"text-indent: 60px\">");
        TAGMAP.put("m3", "<p style=\"text-indent: 90px\">");
        TAGMAP.put("m4", "<p style=\"text-indent: 90px\">");
        TAGMAP.put("m5", "<p style=\"text-indent: 90px\">");
        TAGMAP.put("m6", "<p style=\"text-indent: 90px\">");
        TAGMAP.put("m7", "<p style=\"text-indent: 90px\">");
        TAGMAP.put("m8", "<p style=\"text-indent: 90px\">");
        TAGMAP.put("m9", "<p style=\"text-indent: 90px\">");
        ENDTAGMAP.put("m", "</p>");
        ENDTAGMAP.put("lang", "</span>");
        TAGMAP.put("*", "<details>");
        ENDTAGMAP.put("*", "</details>");
    }
}
