package io.github.eb4j.dsl;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

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

    private static final String EXPECTED_ABANDON =
            "[m1][b]1.[/b] [trn]отказываться [com]([i]от чего-л.[/i])[/com]," +
                    " прекращать [com]([i]попытки и т. п.[/i])[/com][/trn][/m]\n" +
            "[m1][b]2.[/b] [trn]покидать, оставлять[/trn][/m]\n" +
            "[m2]to [ref]abandon attempts[/ref][/m]\n" +
            "[m2]to [ref]abandon a claim[/ref][/m]\n" +
            "[m2]to [ref]abandon convertibility[/ref][/m]\n" +
            "[m2]to [ref]abandon the \\[gold\\] standard[/ref][/m]\n" +
            "[m2]to [ref]abandon price control[/ref][/m]\n" +
            "[m2]to [ref]abandon a right[/ref][/m]\n";

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
                dumper.finish();
                assertEquals(EXPECTED_ABANDON,
                        baos.toString("UTF-8"));
                break;
            }
        }
    }
}