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

public abstract class DslVisitor {
    /** Visit a tag */
    public abstract void visit(DslArticle.Tag tag);

    /** Visit a text */
    public abstract void visit(DslArticle.Text t);

    /**
     * Visit an Attribute
     */
    public abstract void visit(DslArticle.Attribute a);

    /**
     * Visit a NewLine
     */
    public abstract void visit(DslArticle.Newline n);

    /** Visit an EndTag */
    public abstract void visit(DslArticle.EndTag endTag);

    /** Visit an ElementSequence. */
    public void visit(DslArticle.ElementSequence s) {
        for (Iterator<DslArticle.DslElement> iterator = s.iterator(); iterator.hasNext();) {
            DslArticle.DslElement dslElement = iterator.next();
            dslElement.accept(this);
        }
    }

    /** Visit a DslDocument. */
    public void visit(DslArticle d) {
        start();
        visit(d.elements);
        finish();
    }

    /** Start. */
    public void start() {
    }

    /** Finish. */
    public void finish() {
    }
}
