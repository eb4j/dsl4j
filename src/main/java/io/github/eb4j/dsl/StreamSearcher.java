/*
 * DSL4J, a parser library for LingoDSL format.
 * Copyright (C) 2022 Hiroshi Miura.
 * Copyright (C) 2014,Raghu Angadi
 * Copyright (C) 2019 Alan Velasco
 * Copyright (C) 2010-2019 Twitter inc.
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

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * An efficient stream searching class based on the Knuth-Morris-Pratt algorithm.
 * For more on the algorithm works see: https://www.inf.fh-flensburg.de/lang/algorithmen/pattern/kmpen.htm.
 * borrowed from twitter/elephant-bird (apache-2.0)
 */
public class StreamSearcher {

  protected byte[] pattern;
  protected int[] borders;

  public StreamSearcher(final byte[] pattern) {
    setPattern(pattern);
  }

  /**
   * Sets a new pattern for this StreamSearcher to use.
   * @param pattern
   *          the pattern the StreamSearcher will look for in future calls to search(...)
   */
  public void setPattern(final byte[] pattern) {
    this.pattern = Arrays.copyOf(pattern, pattern.length);
    borders = new int[this.pattern.length + 1];
    preProcess();
  }

  /**
   * Searches for the next occurrence of the pattern in the stream, starting from the current stream position. Note
   * that the position of the stream is changed. If a match is found, the stream points to the end of the match
   * -- i.e. the byte AFTER the pattern.
   * Else, the stream is entirely consumed. The latter is because InputStream semantics make it difficult to have
   * another reasonable default, i.e. leave the stream unchanged.
   *
   * @param stream input stream to search
   * @return bytes consumed if found, -1 otherwise.
   * @throws IOException when I/O read error occurred.
   */
  public long search(final InputStream stream) throws IOException {
    long bytesRead = 0;

    int b;
    int j = 0;

    while ((b = stream.read()) != -1) {
      bytesRead++;

      while (j >= 0 && (byte) b != pattern[j]) {
        j = borders[j];
      }
      // Move to the next character in the pattern.
      ++j;

      // If we've matched up to the full pattern length, we found it.  Return,
      // which will automatically save our position in the InputStream at the point immediately
      // following the pattern match.
      if (j == pattern.length) {
        return bytesRead;
      }
    }

    // No dice, Note that the stream is now completely consumed.
    return -1;
  }

  /**
   * Builds up a table of longest "borders" for each prefix of the pattern to find. This table is stored internally
   * and aids in implementation of the Knuth-Moore-Pratt string search.
   * <p>
   * For more information, see: https://www.inf.fh-flensburg.de/lang/algorithmen/pattern/kmpen.htm.
   */
  protected void preProcess() {
    int i = 0;
    int j = -1;
    borders[i] = j;
    while (i < pattern.length) {
      while (j >= 0 && pattern[i] != pattern[j]) {
        j = borders[j];
      }
      borders[++i] = ++j;
    }
  }
}
