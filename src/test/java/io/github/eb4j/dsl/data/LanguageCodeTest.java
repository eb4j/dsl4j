package io.github.eb4j.dsl.data;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LanguageCodeTest {
    @Test
    void constructor() {
        LanguageCode languageCode = new LanguageCode();
        assertEquals("en", languageCode.get(1));
    }
}