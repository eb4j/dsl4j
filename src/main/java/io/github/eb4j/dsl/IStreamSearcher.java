package io.github.eb4j.dsl;

import java.io.IOException;
import java.io.InputStream;

public interface IStreamSearcher {
    long search(InputStream stream) throws IOException;
}
