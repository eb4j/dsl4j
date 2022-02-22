package io.github.eb4j.dsl;

import java.io.IOException;
import java.io.InputStream;

/**
 * A lazy line end searcher for Little endian UTF-16, UTF-8, and ANSI.
 * Just search LF(0x0a) and check UTF-16LE LF (0x0a 0x00).
 */
public class LFStreamSearcher implements IStreamSearcher {
    private static final byte lf = 0x0a;
    private final boolean isUTF16;

    public LFStreamSearcher(final boolean isUTF16) {
        this.isUTF16 = isUTF16;
    }

    public long search(InputStream stream) throws IOException {
        long bytesRead = 0;
        int b;
        while ((b = stream.read()) != -1) {
            bytesRead++;
            if ((byte) b == lf) {
                if (!isUTF16) {
                    return bytesRead;
                }
                bytesRead++;
                if ((b = stream.read()) == -1) {
                    // eof
                    return -1;
                }
                if (b == 0x00) {
                    return bytesRead;
                }
            }
        }
        // no dice
        return -1;
    }
}
