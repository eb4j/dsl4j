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
    void bold() throws ParseException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DslParser parser = DslParser.createParser("[trn][b][c]abc[/c][/b] def [/trn]");
        DslArticle article = parser.DslArticle();
        DslDumper dumper = new DslDumper(baos, StandardCharsets.UTF_8);
        article.accept(dumper);
        dumper.finish();
        assertEquals("[trn][b][c]abc[/c][/b] def [/trn]", baos.toString());
    }

    @Test
    void mean() throws ParseException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DslParser parser = DslParser.createParser("[m]abc[/m]");
        DslArticle article = parser.DslArticle();
        DslDumper dumper = new DslDumper(baos, StandardCharsets.UTF_8);
        article.accept(dumper);
        dumper.finish();
        assertEquals("[m]abc[/m]", baos.toString());
    }

    @Test
    void mean1() throws ParseException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DslParser parser = DslParser.createParser("[m1]abc[/m]");
        DslArticle article = parser.DslArticle();
        DslDumper dumper = new DslDumper(baos, StandardCharsets.UTF_8);
        article.accept(dumper);
        dumper.finish();
        assertEquals("[m1]abc[/m]", baos.toString());
    }

    @Test
    void unicode() throws ParseException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DslParser parser = DslParser.createParser("[trn]same as 一樣|一样 [t]yī yàng[/t], the same[/trn]");
        DslArticle article = parser.DslArticle();
        DslDumper dumper = new DslDumper(baos, StandardCharsets.UTF_8);
        article.accept(dumper);
        dumper.finish();
        assertEquals("[trn]same as 一樣|一样 [t]yī yàng[/t], the same[/trn]", baos.toString());
    }

    @Test
    void complex() throws ParseException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DslParser parser = DslParser.createParser("[m1][b]1.[/b] [trn]отказываться [com]([i]от чего-л.[/i])[/com], прекращать [com]([i]попытки и т. п.[/i])[/com][/trn][/m]");
        DslArticle article = parser.DslArticle();
        DslDumper dumper = new DslDumper(baos, StandardCharsets.UTF_8);
        article.accept(dumper);
        dumper.finish();
        assertEquals("[m1][b]1.[/b] [trn]отказываться [com]([i]от чего-л.[/i])[/com], прекращать [com]([i]попытки и т. п.[/i])[/com][/trn][/m]", baos.toString());
    }

    @Test
    void multiline() throws ParseException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DslParser parser = DslParser.createParser("    [m1][b]1.[/b] [trn]отказываться [com]([i]от чего-л.[/i])[/com], прекращать [com]([i]попытки и т. п.[/i])[/com][/trn][/m]\n    [m2]to [ref]abandon attempts[/ref][/m]");
        DslArticle article = parser.DslArticle();
        DslDumper dumper = new DslDumper(baos, StandardCharsets.UTF_8);
        article.accept(dumper);
        dumper.finish();
        assertEquals("    [m1][b]1.[/b] [trn]отказываться [com]([i]от чего-л.[/i])[/com], прекращать [com]([i]попытки и т. п.[/i])[/com][/trn][/m]\n    [m2]to [ref]abandon attempts[/ref][/m]", baos.toString());
    }

}
