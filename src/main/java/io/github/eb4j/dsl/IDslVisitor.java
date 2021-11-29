package io.github.eb4j.dsl;

public interface IDslVisitor<T> {
    /**
     * Visit a tag.
     * @param tag to visit.
     */
    void visit(DslArticle.Tag tag);

    /**
     * Visit a text.
     * @param t Text object to visit.
     */
    void visit(DslArticle.Text t);

    /**
     * Visit an Attribute.
     * @param a Attribute object to visit.
     */
    void visit(DslArticle.Attribute a);

    /**
     * Visit a NewLine.
     * @param n newline object to visit.
     */
    void visit(DslArticle.Newline n);

    /**
     * Visit an EndTag.
     * @param endTag to visit.
     */
    void visit(DslArticle.EndTag endTag);

    /**
     * Return result.
     * @return T result.
     */
    T getObject();

    /**
     * Visit an ElementSequence.
     * @param s ElementSequence Object to visit.
     */
    void visit(DslArticle.ElementSequence s);

    /**
     * Visit a DslArticle..
     * @param d Root Article to visit.
     */
    void visit(DslArticle d);

    /** Start. */
    void start();

    /** Finish. */
    void finish();

}
