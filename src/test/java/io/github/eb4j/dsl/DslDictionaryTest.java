package io.github.eb4j.dsl;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class DslDictionaryTest {

    @Test
    void loadDicitonary() throws URISyntaxException, IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        File file = new File(Objects.requireNonNull(this.getClass().getResource("/test.dsl")).toURI().getPath());
        DslDictionary.loadDicitonary(file);
    }
}