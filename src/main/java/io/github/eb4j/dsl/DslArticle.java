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

import io.github.eb4j.dsl.visitor.DslVisitor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DslArticle implements Visitable {
    private ElementSequence elements;

    public DslArticle(final ElementSequence elements) {
        this.elements = elements;
    }

    @Override
    public void accept(final DslVisitor v) {
        v.visit(this);
    }

    private static String dequote(final String s) {
        if (s == null) {
            return "";
        }
        if ((s.startsWith("\"") && s.endsWith("\"")) ||
                (s.startsWith("'") && s.endsWith("'"))) {
            return s.substring(1, s.length() - 1);
        }
        return s;
    }

    /**
     * Getter for ElementSequence.
     * @return elements.
     */
    public ElementSequence getElements() {
        return elements;
    }

    /**
     * Abstract class for elements.  Enforces support for Visitors.
     */
    public abstract static class DslElement implements Visitable {
        public abstract void accept(DslVisitor v);
        public abstract String toString();
    }

    /**
     * Class to express Tag [...].
     */
    public static class Tag extends DslElement {
        /**
         * Name of tag.
         */
        private String tagName;
        /**
         * Attribute when exist. Otherwise it is null.
         */
        private Attribute attribute;

        /**
         * constructor.
         * @param t tagName
         * @param a attribute
         */
        public Tag(final String t, final Attribute a) {
            tagName = t;
            attribute = a;
        }

        /**
         * Getter of tagName.
         * @return tag name.
         */
        public String getTagName() {
            return tagName;
        }

        /**
         * Is Tag name?
         * @param name of tag to be checked.
         * @return true if tag is a specified name, otherwise false.
         */
        public boolean isTagName(final String name) {
            return tagName.equals(name);
        }

        /**
         * Getter of attribute.
         * @return Attribute if tag has, otherwise null;
         */
        public Attribute getAttribute() {
            return attribute;
        }

        /**
         * Does tag has attribute?
         * @return true when tag has attribute, otherwise false.
         */
        public boolean hasAttribute() {
            return attribute != null;
        }

        /**
         * Acceptor for visitor.
         * @param v visitor.
         */
        @Override
        public void accept(final DslVisitor v) {
            v.visit(this);
        }

        /**
         * Default string expression.
         * @return string mostly as same as original expression.
         */
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

    /**
     * EndTag class to express [/...].
     */
    public static class EndTag extends DslElement {
        private String tagName;

        public EndTag(final String t) {
            tagName = t;
        }

        /**
         * Getter of tag name.
         * @return tag name.
         */
        public String getTagName() {
            return tagName;
        }

        /**
         * Is Tag name?
         * @param name of tag to be checked.
         * @return true if tag is a specified name, otherwise false.
         */
        public boolean isTagName(final String name) {
            return tagName.equals(name);
        }

        /**
         * Visitor accepter.
         * @param v visitor.
         */
        @Override
        public void accept(final DslVisitor v) {
            v.visit(this);
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

    public static abstract class Attribute {
        protected String name;

        /**
         *  @return the value with quotes removed.
         */
        public String getValue() {
            return dequote(name);
        }

        public abstract String getKey();
    }

    public static class ColorAttribute extends Attribute {
        public ColorAttribute(final String v) {
            name = v;
        }

        @Override
        public String toString() {
            return name;
        }

        @Override
        public String getKey() {
            return null;
        }
    }

    public static class LangAttribute extends Attribute {
        private String key;
        public LangAttribute(final String k, final String v) {
            key = k;
            name = v;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public String toString() {
            return key + "=" + name;
        }
    }

    public static class Text extends DslElement {
        private String text;

        public Text(final String t) {
            text = t;
        }

        public String getText() {
            return text;
        }

        /**
         * Acceptor for text visitor.
         * @param v visitor.
         */
        public void accept(final DslVisitor v) {
            v.visit(this);
        }

        public String toString() {
            return text;
        }
    }

    /**
     * Element sequence class for holding all elements.
     */
    public static class ElementSequence {
        private final List<DslElement> elements;

        /** Constructor. */
        public ElementSequence() {
            elements = new ArrayList<>();
        }

        /**
         * Add element to list.
         * @param o DslElement to add.
         */
        public void addElement(final DslElement o) {
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
        public void setElements(final List<DslElement> collection) {
            elements.clear();
            elements.addAll(collection);
        }
    }

    /**
     * NewLine expressing class.
     */
    public static class Newline extends DslElement {

        private static final String NL = System.getProperty("line.separator");

        /**
         * Acceptor for NL visitor.
         * @param v visitor.
         */
        @Override
        public void accept(final DslVisitor v) {
            v.visit(this);
        }

        /**
         * Return NL.
         * @return new line character.
         */
        public String toString() {
            return NL;
        }
    }
}
