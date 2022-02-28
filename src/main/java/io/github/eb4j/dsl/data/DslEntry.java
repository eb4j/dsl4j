/*
 * DSL4J, a parser library for LingoDSL format.
 * Copyright (C) 2021-2022 Hiroshi Miura.
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

package io.github.eb4j.dsl.data;

public class DslEntry {
    private final long headerOffset;
    private final int headerSize;
    private final long offset;
    private final int size;

    public DslEntry(final long headerOffset, final int headerSize, final long offset, final int size) {
        this.headerOffset = headerOffset;
        this.headerSize = headerSize;
        this.offset = offset;
        this.size = size;
    }

    public long getHeaderOffset() {
        return headerOffset;
    }

    public int getHeaderSize() {
        return headerSize;
    }

    public long getOffset() {
        return offset;
    }

    public int getSize() {
        return size;
    }

    @Override
    @SuppressWarnings("NeedBraces")
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DslEntry dslEntry = (DslEntry) o;

        if (headerOffset != dslEntry.headerOffset) return false;
        if (headerSize != dslEntry.headerSize) return false;
        if (offset != dslEntry.offset) return false;
        return size == dslEntry.size;
    }

    @Override
    public int hashCode() {
        int result = (int) (headerOffset ^ (headerOffset >>> 32));
        result = 31 * result + headerSize;
        result = 31 * result + (int) (offset ^ (offset >>> 32));
        result = 31 * result + size;
        return result;
    }
}
