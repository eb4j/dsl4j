package io.github.eb4j.dsl;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DslDictionaryTest {

    private final URL resource = this.getClass().getResource("/test.dsl");

    @Test
    void loadDicitonarySingle() throws URISyntaxException, IOException, ParseException {
        DslDictionary dictionary = DslDictionary.loadDictionary(new File(resource.toURI()));
        for (Map.Entry<String, Object> entry: dictionary.getEntries("space")) {
            assertEquals("space", entry.getKey());
            Object val = entry.getValue();
            if (val instanceof String) {
                DslParser parser = DslParser.createParser((String) val);
                DslArticle article = parser.DslArticle();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                DslDumper dumper = new DslDumper(baos, StandardCharsets.UTF_8);
                article.accept(dumper);
                dumper.finish();
                assertEquals("[m1][trn]Only a single white space on first character[/trn][/m]\n",
                        baos.toString("UTF-8"));
                break;
            }
        }
    }

    @Test
    void loadDicitonaryMulti() throws URISyntaxException, IOException, ParseException {
        DslDictionary dictionary = DslDictionary.loadDictionary(new File(resource.toURI()));
        for (Map.Entry<String, Object> entry: dictionary.getEntries("abandon")) {
            Object val = entry.getValue();
            if (val instanceof String) {
                DslParser parser = DslParser.createParser((String) val);
                DslArticle article = parser.DslArticle();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                DslDumper dumper = new DslDumper(baos, StandardCharsets.UTF_8);
                article.accept(dumper);
                assertEquals("[m1][b]1.[/b] [trn]отказываться [com]([i]от чего-л.[/i])[/com]," +
                                " прекращать [com]([i]попытки и т. п.[/i])[/com][/trn][/m]\n" +
                                "[m1][b]2.[/b] [trn]покидать, оставлять[/trn][/m]\n" +
                                "[m2]to [ref]abandon attempts[/ref][/m]\n" +
                                "[m2]to [ref]abandon a claim[/ref][/m]\n" +
                                "[m2]to [ref]abandon convertibility[/ref][/m]\n" +
                                "[m2]to [ref]abandon the \\[gold\\] standard[/ref][/m]\n" +
                                "[m2]to [ref]abandon price control[/ref][/m]\n" +
                                "[m2]to [ref]abandon a right[/ref][/m]\n",
                        baos.toString("UTF-8"));
                break;
            }
        }
    }

    @Test
    void loadDictionaryComplexHtml() throws URISyntaxException, IOException, ParseException {
        DslDictionary dictionary = DslDictionary.loadDictionary(new File(resource.toURI()));
        for (Map.Entry<String, Object> entry: dictionary.getEntries("abandon")) {
            Object val = entry.getValue();
            if (val instanceof String) {
                DslParser parser = DslParser.createParser((String) val);
                DslArticle article = parser.DslArticle();
                DslHtmlFilter filter = new DslHtmlFilter();
                article.accept(filter);
                assertEquals("<p style=\"text-indent: 30px\"><strong>1.</strong> \u043E\u0442\u043A\u0430\u0437\u044B\u0432\u0430\u0442\u044C\u0441\u044F" +
                        " (<span style='font-style: italic'>\u043E\u0442 \u0447\u0435\u0433\u043E-\u043B.</span>)," +
                        " \u043F\u0440\u0435\u043A\u0440\u0430\u0449\u0430\u0442\u044C " +
                        "(<span style='font-style: italic'>\u043F\u043E\u043F\u044B\u0442\u043A\u0438 \u0438 \u0442." +
                        " \u043F.</span>)</p>\n" +
                        "<p style=\"text-indent: 30px\"><strong>2.</strong> \u043F\u043E\u043A\u0438\u0434\u0430\u0442\u044C," +
                        " \u043E\u0441\u0442\u0430\u0432\u043B\u044F\u0442\u044C</p>\n" +
                        "<p style=\"text-indent: 60px\">to abandon attempts</p>\n" +
                        "<p style=\"text-indent: 60px\">to abandon a claim</p>\n" +
                        "<p style=\"text-indent: 60px\">to abandon convertibility</p>\n" +
                        "<p style=\"text-indent: 60px\">to abandon the [gold] standard</p>\n" +
                        "<p style=\"text-indent: 60px\">to abandon price control</p>\n" +
                        "<p style=\"text-indent: 60px\">to abandon a right</p>\n",
                filter.toString());
                break;
            }
        }
    }
}