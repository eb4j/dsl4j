package io.github.eb4j.dsl.data;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LanguageNameTest {

    @Test
    void constructor() {
        LanguageName languageName = new LanguageName();
        assertEquals("en", languageName.get("English"));
    }

}
