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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DslArticle implements Visitable {
     ElementSequence elements;

    public DslArticle(ElementSequence elements) {
        this.elements = elements;
    }

    @Override
    public void accept(DslVisitor v) {
        v.visit(this);
    }

    private static String dequote(String s) {
        if (s == null)
            return "";
        if ((s.startsWith("\"") && s.endsWith("\"")) ||
                (s.startsWith("'") && s.endsWith("'")))
            return s.substring(1, s.length()-1);
        else
            return s;
    }

    /**
     * Abstract class for elements.  Enforces support for Visitors.
     */
    public static abstract class DslElement implements Visitable {
        public abstract void accept(DslVisitor v);
        public abstract int getLength();
        public abstract String toString();
    }

    public static class Tag extends DslElement {
        public String tagName;
        public Attribute attribute;
        public Tag(String t, Attribute a) {
            tagName = t;
            attribute = a;
        }

        @Override
        public void accept(DslVisitor v) {
            v.visit(this);
        }

        @Override
        public int getLength() {
            if (attribute != null) {
                return tagName.length() + attribute.getLength() + 2;
            } else {
                return tagName.length() + 2;
            }
        }

        @Override
        public String toString() {
            StringBuffer s = new StringBuffer();
            s.append("[");
            s.append(tagName);
            if (attribute != null) {
                s.append(" ");
                s.append(attribute);
            }
            s.append("]");
            return s.toString();
        }
    }

    public static class EndTag extends DslElement {
        public String tagName;
        public EndTag(String t) {
            tagName = t;
        }

        @Override
        public void accept(DslVisitor v) {
            v.visit(this);
        }

        @Override
        public int getLength() {
            return tagName.length() + 2;
        }

        @Override
        public String toString() {
            StringBuffer s = new StringBuffer();
            s.append("[/");
            s.append(tagName);
            s.append("]");
            return s.toString();
        }
    }

    public static abstract class Attribute extends DslElement {
        public String name;
        /** @return the value with quotes removed */
        public String getValue() {
            return dequote(name);
        }

        @Override
        public void accept(DslVisitor v) {
            v.visit(this);
        }
    }

    public static class ColorAttribute extends Attribute {
        public ColorAttribute(String v) {
            name = v;
        }

        public String get() {
            return name;
        }

        public int getLength() {
            return name.length() + 2;
        }

        @Override
        public String toString() {
            StringBuffer s = new StringBuffer();
            s.append("color=\"");
            s.append(name);
            s.append("\"");
            return s.toString();
        }
    }

    public static class LangAttribute extends Attribute {
        public LangAttribute(String k, String v) {
            if (k.equals("name")) {
                name = v;
            } else {
                // FIXME
                name = "";
            }
        }

        public String get() {
            return name;
        }

        @Override
        public String toString() {
            StringBuffer s = new StringBuffer();
            s.append("name=");
            s.append(name);
            return s.toString();
        }

        public int getLength() {
            return 5 + name.length() + 2;
        }
    }

    public static class Text extends DslElement {
        public String text;
        public Text(String t) {
            text = t;
        }

        public void accept(DslVisitor v) {
            v.visit(this);
        }

        public int getLength() {
            return text.length();
        }

        public String toString() {
            return text;
        }
    }

    public static class ElementSequence {
        private final List<DslElement> elements;

        /** Constructor. */
        public ElementSequence() {
            elements = new ArrayList<>();
        }

        /** Add element to list. */
        public void addElement(DslElement o) {
            elements.add(o);
        }
        /**
         * @return the number of elements in this list.
         */
        public int size() {
            return elements.size();
        }

        /**
         * @return an iterator over the elements in this list in proper sequence.
         */
        public Iterator<DslElement> iterator() {
            return elements.iterator();
        }

        /**
         * Clear current elements and replace with given Collection.
         *
         * @param collection to replace elements with
         */
        public void setElements(List<DslElement> collection) {
            elements.clear();
            elements.addAll(collection);
        }
    }

    public static class Newline extends DslElement {
        public static final String NL = System.getProperty("line.separator");

        @Override
        public void accept(DslVisitor v) {
            v.visit(this);
        }

        public int getLength() {
            return NL.length();
        }

        public String toString() {
            return NL;
        }
    }
}
