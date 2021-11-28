package io.github.eb4j.dsl;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DslParserTest {

    @Test
    void simple() throws ParseException, UnsupportedEncodingException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DslParser parser = DslParser.createParser("[trn]abc[/trn]");
        DslArticle article = parser.DslArticle();
        DslDumper dumper = new DslDumper(baos, StandardCharsets.UTF_8);
        article.accept(dumper);
        dumper.finish();
        assertEquals("[trn]abc[/trn]", baos.toString("UTF-8"));
    }

    @Test
    void nested() throws ParseException, UnsupportedEncodingException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DslParser parser = DslParser.createParser("[trn][c]abc[/c][/trn]");
        DslArticle article = parser.DslArticle();
        DslDumper dumper = new DslDumper(baos, StandardCharsets.UTF_8);
        article.accept(dumper);
        dumper.finish();
        assertEquals("[trn][c]abc[/c][/trn]", baos.toString("UTF-8"));
    }

    @Test
    void color() throws ParseException, UnsupportedEncodingException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DslParser parser = DslParser.createParser("[trn][c green]abc[/c][/trn]");
        DslArticle article = parser.DslArticle();
        DslDumper dumper = new DslDumper(baos, StandardCharsets.UTF_8);
        article.accept(dumper);
        dumper.finish();
        assertEquals("[trn][c color=\"green\"]abc[/c][/trn]", baos.toString("UTF-8"));
    }

    @Test
    void lang() throws ParseException, UnsupportedEncodingException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DslParser parser = DslParser.createParser("[trn][lang name=\"Russian\"]abc[/lang][/trn]");
        DslArticle article = parser.DslArticle();
        DslDumper dumper = new DslDumper(baos, StandardCharsets.UTF_8);
        article.accept(dumper);
        dumper.finish();
        assertEquals("[trn][lang name=\"Russian\"]abc[/lang][/trn]", baos.toString("UTF-8"));
    }

    @Test
    void bold() throws ParseException, UnsupportedEncodingException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DslParser parser = DslParser.createParser("[trn][b][c]abc[/c][/b] def [/trn]");
        DslArticle article = parser.DslArticle();
        DslDumper dumper = new DslDumper(baos, StandardCharsets.UTF_8);
        article.accept(dumper);
        dumper.finish();
        assertEquals("[trn][b][c]abc[/c][/b] def [/trn]", baos.toString("UTF-8"));
    }

    @Test
    void mean() throws ParseException, UnsupportedEncodingException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DslParser parser = DslParser.createParser("[m]abc[/m]");
        DslArticle article = parser.DslArticle();
        DslDumper dumper = new DslDumper(baos, StandardCharsets.UTF_8);
        article.accept(dumper);
        dumper.finish();
        assertEquals("[m]abc[/m]", baos.toString("UTF-8"));
    }

    @Test
    void mean1() throws ParseException, UnsupportedEncodingException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DslParser parser = DslParser.createParser("[m1]abc[/m]");
        DslArticle article = parser.DslArticle();
        DslDumper dumper = new DslDumper(baos, StandardCharsets.UTF_8);
        article.accept(dumper);
        dumper.finish();
        assertEquals("[m1]abc[/m]", baos.toString("UTF-8"));
    }

    @Test
    void unicode() throws ParseException, UnsupportedEncodingException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DslParser parser = DslParser.createParser("[trn]same as 一樣|一样 [t]yī yàng[/t], the same[/trn]");
        DslArticle article = parser.DslArticle();
        DslDumper dumper = new DslDumper(baos, StandardCharsets.UTF_8);
        article.accept(dumper);
        dumper.finish();
        assertEquals("[trn]same as 一樣|一样 [t]yī yàng[/t], the same[/trn]", baos.toString("UTF-8"));
    }

    @Test
    void complex() throws ParseException, UnsupportedEncodingException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DslParser parser = DslParser.createParser("[m1][b]1.[/b] [trn]отказываться [com]([i]от чего-л.[/i])[/com], прекращать [com]([i]попытки и т. п.[/i])[/com][/trn][/m]");
        DslArticle article = parser.DslArticle();
        DslDumper dumper = new DslDumper(baos, StandardCharsets.UTF_8);
        article.accept(dumper);
        dumper.finish();
        assertEquals("[m1][b]1.[/b] [trn]отказываться [com]([i]от чего-л.[/i])[/com], прекращать [com]([i]попытки и т. п.[/i])[/com][/trn][/m]",
                baos.toString("UTF-8"));
    }

    @Test
    void multiline() throws ParseException, UnsupportedEncodingException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DslParser parser = DslParser.createParser("    [m1][b]1.[/b] [trn]отказываться [com]([i]от чего-л.[/i])[/com], прекращать [com]([i]попытки и т. п.[/i])[/com][/trn][/m]\n    [m2]to [ref]abandon attempts[/ref][/m]");
        DslArticle article = parser.DslArticle();
        DslDumper dumper = new DslDumper(baos, StandardCharsets.UTF_8);
        article.accept(dumper);
        dumper.finish();
        assertEquals("    [m1][b]1.[/b] [trn]отказываться [com]([i]от чего-л.[/i])[/com], прекращать [com]([i]попытки и т. п.[/i])[/com][/trn][/m]\n    [m2]to [ref]abandon attempts[/ref][/m]",
                baos.toString("UTF-8"));
    }

}
