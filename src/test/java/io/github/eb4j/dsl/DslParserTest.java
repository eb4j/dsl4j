package io.github.eb4j.dsl;

import io.github.eb4j.dsl.visitor.DumpDslVisitor;
import io.github.eb4j.dsl.visitor.PlainDslVisitor;
import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DslParserTest {

    @Test
    void plain() throws ParseException {
        DslParser parser = DslParser.createParser("[trn]abc[/trn]");
        DslArticle article = parser.DslArticle();
        PlainDslVisitor v = new PlainDslVisitor();
        article.accept(v);
        v.finish();
        assertEquals("abc", v.getObject());
    }

    @Test
    void simple() throws ParseException {
        DslParser parser = DslParser.createParser("[trn]abc[/trn]");
        DslArticle article = parser.DslArticle();
        DumpDslVisitor dumper = new DumpDslVisitor();
        article.accept(dumper);
        dumper.finish();
        assertEquals("[trn]abc[/trn]", dumper.getObject());
    }

    @Test
    void nested() throws ParseException {
        DslParser parser = DslParser.createParser("[trn][c]abc[/c][/trn]");
        DslArticle article = parser.DslArticle();
        DumpDslVisitor dumper = new DumpDslVisitor();
        article.accept(dumper);
        dumper.finish();
        assertEquals("[trn][c]abc[/c][/trn]", dumper.getObject());
    }

    @Test
    void color() throws ParseException, UnsupportedEncodingException {
        DslParser parser = DslParser.createParser("[trn][c green]abc[/c][/trn]");
        DslArticle article = parser.DslArticle();
        DumpDslVisitor dumper = new DumpDslVisitor();
        article.accept(dumper);
        dumper.finish();
        assertEquals("[trn][c color=\"green\"]abc[/c][/trn]", dumper.getObject());
    }

    @Test
    void lang() throws ParseException {
        DslParser parser = DslParser.createParser("[trn][lang name=\"Russian\"]abc[/lang][/trn]");
        DslArticle article = parser.DslArticle();
        DumpDslVisitor dumper = new DumpDslVisitor();
        article.accept(dumper);
        dumper.finish();
        assertEquals("[trn][lang name=\"Russian\"]abc[/lang][/trn]",dumper.getObject());
    }

    @Test
    void bold() throws ParseException {
        DslParser parser = DslParser.createParser("[trn][b][c]abc[/c][/b] def [/trn]");
        DslArticle article = parser.DslArticle();
        DumpDslVisitor dumper = new DumpDslVisitor();
        article.accept(dumper);
        dumper.finish();
        assertEquals("[trn][b][c]abc[/c][/b] def [/trn]", dumper.getObject());
    }

    @Test
    void mean() throws ParseException, UnsupportedEncodingException {
        DslParser parser = DslParser.createParser("[m]abc[/m]");
        DslArticle article = parser.DslArticle();
        DumpDslVisitor dumper = new DumpDslVisitor();
        article.accept(dumper);
        dumper.finish();
        assertEquals("[m]abc[/m]", dumper.getObject());
    }

    @Test
    void mean1() throws ParseException, UnsupportedEncodingException {
        DslParser parser = DslParser.createParser("[m1]abc[/m]");
        DslArticle article = parser.DslArticle();
        DumpDslVisitor dumper = new DumpDslVisitor();
        article.accept(dumper);
        dumper.finish();
        assertEquals("[m1]abc[/m]", dumper.getObject());
    }

    @Test
    void unicode() throws ParseException, UnsupportedEncodingException {
        DslParser parser = DslParser.createParser("[trn]same as 一樣|一样 [t]yī yàng[/t], the same[/trn]");
        DslArticle article = parser.DslArticle();
        DumpDslVisitor dumper = new DumpDslVisitor();
        article.accept(dumper);
        dumper.finish();
        assertEquals("[trn]same as 一樣|一样 [t]yī yàng[/t], the same[/trn]", dumper.getObject());
    }

    @Test
    void complex() throws ParseException, UnsupportedEncodingException {
        DslParser parser = DslParser.createParser("[m1][b]1.[/b] [trn]отказываться [com]([i]от чего-л.[/i])[/com], прекращать [com]([i]попытки и т. п.[/i])[/com][/trn][/m]");
        DslArticle article = parser.DslArticle();
        DumpDslVisitor dumper = new DumpDslVisitor();
        article.accept(dumper);
        dumper.finish();
        assertEquals("[m1][b]1.[/b] [trn]отказываться [com]([i]от чего-л.[/i])[/com], прекращать [com]([i]попытки и т. п.[/i])[/com][/trn][/m]",
                dumper.getObject());
    }

    @Test
    void multiline() throws ParseException, UnsupportedEncodingException {
        DslParser parser = DslParser.createParser("    [m1][b]1.[/b] [trn]отказываться [com]([i]от чего-л.[/i])[/com], прекращать [com]([i]попытки и т. п.[/i])[/com][/trn][/m]\n    [m2]to [ref]abandon attempts[/ref][/m]");
        DslArticle article = parser.DslArticle();
        DumpDslVisitor dumper = new DumpDslVisitor();
        article.accept(dumper);
        dumper.finish();
        assertEquals("    [m1][b]1.[/b] [trn]отказываться [com]([i]от чего-л.[/i])[/com], прекращать [com]([i]попытки и т. п.[/i])[/com][/trn][/m]\n    [m2]to [ref]abandon attempts[/ref][/m]",
                dumper.getObject());
    }

}
