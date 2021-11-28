package io.github.eb4j.dsl;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public class DslHtmlFilter extends DslVisitor {

    private static StringBuilder sb = new StringBuilder();
    private String lastTag;

    public DslHtmlFilter() {
        lastTag = "";
    }

    public String toString() {
        return sb.toString();
    }

    /**
     * Visit a tag
     *
     * @param tag
     */
    @Override
    public void visit(DslArticle.Tag tag) {
        if (tag.tagName.equals("b")) {
            sb.append("<strong>");
        } else if (tag.tagName.equals("u")) {
            sb.append("<span style='text-decoration:underline'>");
        } else if (tag.tagName.equals("i")) {
            sb.append("<span style='font-style: italic'>");
        } else if (tag.tagName.equals("t")) {
            sb.append("<span>");
        } else if (tag.tagName.equals("sup")) {
            sb.append("<sup>");
        } else if (tag.tagName.equals("sub")) {
            sb.append("<sub>");
        } else if (tag.tagName.equals("m")) {
            sb.append("<p style=\"text-indent: 30px\">");
        } else if (tag.tagName.equals("m1")) {
            sb.append("<p style=\"text-indent: 30px\">");
        } else if (tag.tagName.equals("m2")) {
            sb.append("<p style=\"text-indent: 60px\">");
        } else if (tag.tagName.equals("m3")) {
            sb.append("<p style=\"text-indent: 90px\">");
        } else if (tag.tagName.equals("m4")) {
            sb.append("<p style=\"text-indent: 90px\">");
        } else if (tag.tagName.equals("m5")) {
            sb.append("<p style=\"text-indent: 90px\">");
        } else if (tag.tagName.equals("m6")) {
            sb.append("<p style=\"text-indent: 90px\">");
        } else if (tag.tagName.equals("m7")) {
            sb.append("<p style=\"text-indent: 90px\">");
        } else if (tag.tagName.equals("m8")) {
            sb.append("<p style=\"text-indent: 90px\">");
        } else if (tag.tagName.equals("m9")) {
            sb.append("<p style=\"text-indent: 90px\">");
        } else if (tag.tagName.equals("c")) {
            if (tag.attribute != null) {
                sb.append("<span style=\"color: ").append(tag.attribute.name).append("\">");
            } else {
                sb.append("<span style=\"color: green\">");
            }
        } else if (tag.tagName.equals("url")) {
            sb.append("<a href=\"");
        }
        lastTag = tag.tagName;
    }

    /**
     * Visit an EndTag
     *
     * @param endTag
     */
    @Override
    public void visit(DslArticle.EndTag endTag) {

        if (endTag.tagName.equals("b")) {
            sb.append("</strong>");
        } else if (endTag.tagName.equals("u")) {
            sb.append("</span>");
        } else if (endTag.tagName.equals("i")) {
            sb.append("</span>");
        } else if (endTag.tagName.equals("t")) {
            sb.append("&nbsp;</span>");
        } else if (endTag.tagName.equals("sup")) {
            sb.append("</sup>");
        } else if (endTag.tagName.equals("sub")) {
            sb.append("</sub>");
        } else if (endTag.tagName.equals("m")) {
            sb.append("</p>");
        } else if (endTag.tagName.equals("c")) {
            sb.append("</span>");
        } else if (endTag.tagName.equals("url")) {
            sb.append("\">LINK</a>");
        }
    }

    /**
     * Visit a text
     *
     * @param t
     */
    @Override
    public void visit(DslArticle.Text t) {
        sb.append(t);
    }

    /**
     * Visit an Attribute
     *
     * @param a
     */
    @Override
    public void visit(DslArticle.Attribute a) {
    }

    /**
     * Visit a NewLine
     *
     * @param n
     */
    @Override
    public void visit(DslArticle.Newline n) {
        sb.append("\n");
    }
}
