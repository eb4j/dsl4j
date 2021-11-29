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

import java.util.Iterator;

/**
 * Abstract visitor.
 * @param <T> object to get from visitor.
 */
public abstract class AbstractDslVisitor<T> implements IDslVisitor<T> {

    public AbstractDslVisitor() {
    }

    /**
     * Visit a tag.
     * @param tag to visit.
     */
    public abstract void visit(DslArticle.Tag tag);

    /**
     * Visit a text.
     * @param t Text object to visit.
     */
    public abstract void visit(DslArticle.Text t);

    /**
     * Visit an Attribute.
     * @param a Attribute object to visit.
     */
    public abstract void visit(DslArticle.Attribute a);

    /**
     * Visit a NewLine.
     * @param n newline object to visit.
     */
    public abstract void visit(DslArticle.Newline n);

    /**
     * Visit an EndTag.
     * @param endTag to visit.
     */
    public abstract void visit(DslArticle.EndTag endTag);

    /**
     * Return result.
     * @param <T> Type.
     * @return result.
     */
    public abstract T getObject();

    /**
     * Visit an ElementSequence.
     * @param s ElementSequence Object to visit.
     */
    public void visit(final DslArticle.ElementSequence s) {
        for (Iterator<DslArticle.DslElement> iterator = s.iterator(); iterator.hasNext();) {
            DslArticle.DslElement dslElement = iterator.next();
            dslElement.accept(this);
        }
    }

    /**
     * Visit a DslArticle..
     * @param d Root Article to visit.
     */
    public void visit(final DslArticle d) {
        start();
        visit(d.getElements());
        finish();
    }

    /** Start. */
    public void start() {
    }

    /** Finish. */
    public void finish() {
    }

}
