package io.github.eb4j.dsl.visitor;

import io.github.eb4j.dsl.DslArticle;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Plain text visitor.
 */
public class PlainDslVisitor extends DslVisitor<String> {
    protected PrintWriter out;
    protected ByteArrayOutputStream baos = new ByteArrayOutputStream();
    protected Charset charset;

    /**
     * Constructor.
     */
    public PlainDslVisitor() {
        charset = StandardCharsets.UTF_8;
        out = new PrintWriter(new OutputStreamWriter(baos, charset));
    }


    /**
     * Visit a tag.
     *
     * @param tag to visit.
     */
    @Override
    public void visit(final DslArticle.Tag tag) {
    }

    /**
     * Visit a text.
     *
     * @param t Text object to visit.
     */
    @Override
    public void visit(final DslArticle.Text t) {
        out.print(t.getText());
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
     * @param n newline object to visit.
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
    }

    @Override
    public void finish() {
        out.flush();
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
