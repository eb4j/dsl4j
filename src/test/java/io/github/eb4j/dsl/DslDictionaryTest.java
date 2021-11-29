package io.github.eb4j.dsl;

import io.github.eb4j.dsl.visitor.DumpDslVisitor;
import io.github.eb4j.dsl.visitor.HtmlDslVisitor;
import io.github.eb4j.dsl.visitor.PlainDslVisitor;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DslDictionaryTest {

    private final URL resource = this.getClass().getResource("/test.dsl");

    @Test
    void loadDicitonarySingle() throws URISyntaxException, IOException {
        DslDictionary dictionary = DslDictionary.loadDictionary(new File(resource.toURI()));
        DumpDslVisitor dumper = new DumpDslVisitor();
        DslResult results = dictionary.lookup("space");
        for (Map.Entry<String, String> entry: results.getEntries(dumper)) {
            assertEquals("space", entry.getKey());
            assertEquals("[m1][trn]Only a single white space on first character[/trn][/m]\n",
                    entry.getValue());
            break;
        }
    }

    @Test
    void loadDicitonaryMulti() throws URISyntaxException, IOException {
        DslDictionary dictionary = DslDictionary.loadDictionary(new File(resource.toURI()));
        DslResult res = dictionary.lookup("abandon");

        PlainDslVisitor plainFilter = new PlainDslVisitor();
        for (Map.Entry<String, String> entry: res.getEntries(plainFilter)) {
            assertEquals("1. отказываться (от чего-л.)," +
                    " прекращать (попытки и т. п.)\n" +
                    "2. покидать, оставлять\n" +
                    "to abandon attempts\n" +
                    "to abandon a claim\n" +
                    "to abandon convertibility\n" +
                    "to abandon the [gold] standard\n" +
                    "to abandon price control\n" +
                    "to abandon a right\n", entry.getValue());
            break;
        }

        DumpDslVisitor dumper = new DumpDslVisitor();
        for (Map.Entry<String, String> entry: res.getEntries(dumper)) {
            assertEquals("[m1][b]1.[/b] [trn]отказываться [com]([i]от чего-л.[/i])[/com]," +
                            " прекращать [com]([i]попытки и т. п.[/i])[/com][/trn][/m]\n" +
                            "[m1][b]2.[/b] [trn]покидать, оставлять[/trn][/m]\n" +
                            "[m2]to [ref]abandon attempts[/ref][/m]\n" +
                            "[m2]to [ref]abandon a claim[/ref][/m]\n" +
                            "[m2]to [ref]abandon convertibility[/ref][/m]\n" +
                            "[m2]to [ref]abandon the \\[gold\\] standard[/ref][/m]\n" +
                            "[m2]to [ref]abandon price control[/ref][/m]\n" +
                            "[m2]to [ref]abandon a right[/ref][/m]\n", entry.getValue());
            break;
        }

        HtmlDslVisitor filter = new HtmlDslVisitor();
        for (Map.Entry entry: res.getEntries(filter)) {
            assertEquals("<p style=\"text-indent: 30px\"><strong>1.</strong>" +
                    " \u043E\u0442\u043A\u0430\u0437\u044B\u0432\u0430\u0442\u044C\u0441\u044F" +
                    " (<span style='font-style: italic'>\u043E\u0442 \u0447\u0435\u0433\u043E-\u043B.</span>)," +
                    " \u043F\u0440\u0435\u043A\u0440\u0430\u0449\u0430\u0442\u044C " +
                    "(<span style='font-style: italic'>\u043F\u043E\u043F\u044B\u0442\u043A\u0438 \u0438 \u0442." +
                    " \u043F.</span>)</p>\n" +
                    "<p style=\"text-indent: 30px\"><strong>2.</strong>" +
                    " \u043F\u043E\u043A\u0438\u0434\u0430\u0442\u044C," +
                    " \u043E\u0441\u0442\u0430\u0432\u043B\u044F\u0442\u044C</p>\n" +
                    "<p style=\"text-indent: 60px\">to abandon attempts</p>\n" +
                    "<p style=\"text-indent: 60px\">to abandon a claim</p>\n" +
                    "<p style=\"text-indent: 60px\">to abandon convertibility</p>\n" +
                    "<p style=\"text-indent: 60px\">to abandon the [gold] standard</p>\n" +
                    "<p style=\"text-indent: 60px\">to abandon price control</p>\n" +
                    "<p style=\"text-indent: 60px\">to abandon a right</p>\n", entry.getValue());
            break;
        }
    }
}
