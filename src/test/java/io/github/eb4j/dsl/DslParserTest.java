package io.github.eb4j.dsl;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DslParserTest {

    @Test
    void readSimple() throws ParseException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DslParser parser = DslParser.createParser("[trn]abc[/trn]");
        DslArticle article = parser.DslArticle();
        DslDumper dumper = new DslDumper(baos, StandardCharsets.UTF_8);
        article.accept(dumper);
        dumper.finish();
        assertEquals("[trn]abc[/trn]", baos.toString());
    }

    @Test
    void readNexted() throws ParseException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DslParser parser = DslParser.createParser("[trn][c]abc[/c][/trn]");
        DslArticle article = parser.DslArticle();
        DslDumper dumper = new DslDumper(baos, StandardCharsets.UTF_8);
        article.accept(dumper);
        dumper.finish();
        assertEquals("[trn][c]abc[/c][/trn]", baos.toString());
    }
}
