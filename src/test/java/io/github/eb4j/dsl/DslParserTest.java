package io.github.eb4j.dsl;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DslParserTest {

    @Test
    void simple() throws ParseException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DslParser parser = DslParser.createParser("[trn]abc[/trn]");
        DslArticle article = parser.DslArticle();
        DslDumper dumper = new DslDumper(baos, StandardCharsets.UTF_8);
        article.accept(dumper);
        dumper.finish();
        assertEquals("[trn]abc[/trn]", baos.toString());
    }

    @Test
    void nested() throws ParseException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DslParser parser = DslParser.createParser("[trn][c]abc[/c][/trn]");
        DslArticle article = parser.DslArticle();
        DslDumper dumper = new DslDumper(baos, StandardCharsets.UTF_8);
        article.accept(dumper);
        dumper.finish();
        assertEquals("[trn][c]abc[/c][/trn]", baos.toString());
    }

    @Test
    void color() throws ParseException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DslParser parser = DslParser.createParser("[trn][c green]abc[/c][/trn]");
        DslArticle article = parser.DslArticle();
        DslDumper dumper = new DslDumper(baos, StandardCharsets.UTF_8);
        article.accept(dumper);
        dumper.finish();
        assertEquals("[trn][c color=\"green\"]abc[/c][/trn]", baos.toString());
    }

    @Test
    void lang() throws ParseException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DslParser parser = DslParser.createParser("[trn][lang name=\"Russian\"]abc[/lang][/trn]");
        DslArticle article = parser.DslArticle();
        DslDumper dumper = new DslDumper(baos, StandardCharsets.UTF_8);
        article.accept(dumper);
        dumper.finish();
        assertEquals("[trn][lang name=\"Russian\"]abc[/lang][/trn]", baos.toString());
    }


    @Test
    void mean() throws ParseException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DslParser parser = DslParser.createParser("[m][trn][b][c]abc[/c][/b] def [/trn][/m]");
        DslArticle article = parser.DslArticle();
        DslDumper dumper = new DslDumper(baos, StandardCharsets.UTF_8);
        article.accept(dumper);
        dumper.finish();
        assertEquals("[trn][c color=\"green\"]abc[/c][/trn]", baos.toString());
    }

}
