package io.github.eb4j.dsl;

import io.github.eb4j.dsl.visitor.DumpDslVisitor;
import io.github.eb4j.dsl.visitor.HtmlDslVisitor;
import io.github.eb4j.dsl.visitor.PlainDslVisitor;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class DslParserTest {

    @Test
    void plain() throws ParseException {
        DslParser parser = DslParser.createParser("[trn]abc[/trn]");
        DslArticle article = parser.DslArticle();
        PlainDslVisitor visitor = new PlainDslVisitor();
        article.accept(visitor);
        assertEquals("abc", visitor.getObject());
    }

    @Test
    void simple() throws ParseException {
        DslParser parser = DslParser.createParser("[trn]abc[/trn]");
        DslArticle article = parser.DslArticle();
        DumpDslVisitor dumper = new DumpDslVisitor();
        article.accept(dumper);
        assertEquals("[trn]abc[/trn]", dumper.getObject());
    }

    @Test
    void nested() throws ParseException {
        DslParser parser = DslParser.createParser("[trn][c]abc[/c][/trn]");
        DslArticle article = parser.DslArticle();
        DumpDslVisitor dumper = new DumpDslVisitor();
        article.accept(dumper);
        assertEquals("[trn][c]abc[/c][/trn]", dumper.getObject());
    }

    @Test
    void color() throws ParseException {
        DslParser parser = DslParser.createParser("[trn][c green]abc[/c][/trn]");
        DslArticle article = parser.DslArticle();
        DumpDslVisitor dumper = new DumpDslVisitor();
        article.accept(dumper);
        assertEquals("[trn][c green]abc[/c][/trn]", dumper.getObject());
    }

    @Test
    void lang() throws ParseException {
        DslParser parser = DslParser.createParser("[trn][lang name=\"Russian\"]abc[/lang][/trn]");
        DslArticle article = parser.DslArticle();
        DumpDslVisitor dumper = new DumpDslVisitor();
        article.accept(dumper);
        assertEquals("[trn][lang name=\"Russian\"]abc[/lang][/trn]", dumper.getObject());
    }

    @Test
    void langId() throws ParseException, IOException {
        DslParser parser = DslParser.createParser("[trn][lang id=1]abc[/lang][/trn]");
        DslArticle article = parser.DslArticle();
        DumpDslVisitor visitor = new DumpDslVisitor();
        article.accept(visitor);
        assertEquals("[trn][lang id=1]abc[/lang][/trn]", visitor.getObject());
    }

    @Test
    void bold() throws ParseException {
        DslParser parser = DslParser.createParser("[trn][b][c]abc[/c][/b] def [/trn]");
        DslArticle article = parser.DslArticle();
        DumpDslVisitor dumper = new DumpDslVisitor();
        article.accept(dumper);
        assertEquals("[trn][b][c]abc[/c][/b] def [/trn]", dumper.getObject());
    }

    @Test
    void mean() throws ParseException {
        DslParser parser = DslParser.createParser("[m]abc[/m]");
        DslArticle article = parser.DslArticle();
        DumpDslVisitor dumper = new DumpDslVisitor();
        article.accept(dumper);
        assertEquals("[m]abc[/m]", dumper.getObject());
    }

    @Test
    void mean1() throws ParseException {
        DslParser parser = DslParser.createParser("[m1]abc[/m]");
        DslArticle article = parser.DslArticle();
        DumpDslVisitor dumper = new DumpDslVisitor();
        article.accept(dumper);
        assertEquals("[m1]abc[/m]", dumper.getObject());
    }

    @Test
    void unicode() throws ParseException {
        DslParser parser = DslParser.createParser("[trn]same as ??????|?????? [t]y?? y??ng[/t], the same[/trn]");
        DslArticle article = parser.DslArticle();
        DumpDslVisitor dumper = new DumpDslVisitor();
        article.accept(dumper);
        assertEquals("[trn]same as ??????|?????? [t]y?? y??ng[/t], the same[/trn]", dumper.getObject());
    }

    @Test
    void complex() throws ParseException {
        DslParser parser = DslParser.createParser("[m1][b]1.[/b] [trn]???????????????????????? [com]([i]???? ????????-??.[/i])[/com]," +
                " ???????????????????? [com]([i]?????????????? ?? ??. ??.[/i])[/com][/trn][/m]");
        DslArticle article = parser.DslArticle();
        DumpDslVisitor dumper = new DumpDslVisitor();
        article.accept(dumper);
        assertEquals("[m1][b]1.[/b] [trn]???????????????????????? [com]([i]???? ????????-??.[/i])[/com]," +
                        " ???????????????????? [com]([i]?????????????? ?? ??. ??.[/i])[/com][/trn][/m]",
                dumper.getObject());
    }

    @Test
    void multiline() throws ParseException {
        DslParser parser = DslParser.createParser("    [m1][b]1.[/b] [trn]???????????????????????? [com]([i]???? ????????-??.[/i])[/com]," +
                " ???????????????????? [com]([i]?????????????? ?? ??. ??.[/i])[/com][/trn][/m]\n" +
                "    [m2]to [ref]abandon attempts[/ref][/m]");
        DslArticle article = parser.DslArticle();
        DumpDslVisitor dumper = new DumpDslVisitor();
        article.accept(dumper);
        assertEquals("    [m1][b]1.[/b] [trn]???????????????????????? [com]([i]???? ????????-??.[/i])[/com]," +
                        " ???????????????????? [com]([i]?????????????? ?? ??. ??.[/i])[/com][/trn][/m]\n" +
                        "    [m2]to [ref]abandon attempts[/ref][/m]",
                dumper.getObject());
    }

    @Test
    void media() throws ParseException, IOException {
        DslParser parser = DslParser.createParser(
                "      [trn]this is media [s]image.jpg[/s]  image and [video]video.ogv[/video][/trn]");
        DslArticle article = parser.DslArticle();
        File current = new File(".");
        HtmlDslVisitor visitor = new HtmlDslVisitor(current.getPath());
        article.accept(visitor);
        assertEquals(
                "      this is media <img src=\"file:" + new File(current, "image.jpg").getAbsolutePath()
                        + "\" />  image and <a href=\"file:" + new File(current, "video.ogv").getAbsolutePath()
                        + "\">video.ogv</a>",
                visitor.getObject());
    }

    @Test
    void colorHtml() throws ParseException {
        DslParser parser = DslParser.createParser("[c blue]abc[/c]");
        DslArticle article = parser.DslArticle();
        HtmlDslVisitor visitor = new HtmlDslVisitor();
        article.accept(visitor);
        assertEquals("<span style=\"color: blue\">abc</span>", visitor.getObject());
    }

    @Test
    void langHtml() throws ParseException {
        DslParser parser = DslParser.createParser("[lang id=1]abc[/lang]");
        DslArticle article = parser.DslArticle();
        HtmlDslVisitor visitor = new HtmlDslVisitor();
        article.accept(visitor);
        assertEquals("<span class=\"lang_en\">abc</span>", visitor.getObject());
    }

    @Test
    void asteriskHtml() throws ParseException {
        DslParser parser = DslParser.createParser("[*]abc[/*]");
        DslArticle article = parser.DslArticle();
        HtmlDslVisitor visitor = new HtmlDslVisitor();
        article.accept(visitor);
        assertEquals("<span class=\"details\">abc</span>", visitor.getObject());
    }

}
