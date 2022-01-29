package io.github.eb4j.dsl;

import io.github.eb4j.dsl.visitor.DumpDslVisitor;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DslDictzipTest {

    private final URL resource = this.getClass().getResource("/test1.dsl.dz");

    @Test
    void loadDicitonaryIndexFromDz() throws URISyntaxException, IOException {
        Path dictPath = Paths.get(resource.toURI());
        DslDictionary dictionary = DslDictionary.loadDictionary(dictPath);
        assertEquals("Test (En-Ru)", dictionary.getDictionaryName());
        assertEquals("English", dictionary.getIndexLanguage());
        assertEquals("Russian", dictionary.getContentLanguage());
        DumpDslVisitor dumper = new DumpDslVisitor();
        for (Map.Entry<String, String> entry : dictionary.lookup("space").getEntries(dumper)) {
            assertEquals("space", entry.getKey());
            assertEquals("[m1][trn]Only a single white space on first character[/trn][/m]\n",
                    entry.getValue());
            break;
        }
        for (Map.Entry<String, String> entry : dictionary.lookup("tab").getEntries(dumper)) {
            assertEquals("tab", entry.getKey());
            assertEquals("[m1][trn]Translation line also can have a single TAB char[/trn][/m]\n",
                    entry.getValue());
            break;
        }
        for (Map.Entry<String, String> entry : dictionary.lookup("tag").getEntries(dumper)) {
            assertEquals("tag", entry.getKey());
            assertEquals("[m1][trn]tag should be ignored[/trn][/m]\n", entry.getValue());
            break;
        }
        dumper = new DumpDslVisitor();
        for (Map.Entry<String, String> entry : dictionary.lookup("abandon").getEntries(dumper)) {
            assertEquals("[m1][b]1.[/b] [trn]отказываться [com]([i]от чего-л.[/i])[/com]," +
                    " прекращать [com]([i]попытки и т. п.[/i])[/com][/trn][/m]\n" +
                    "[m1][b]2.[/b] [trn]покидать, оставлять[/trn][/m]\n" +
                    "[m2]to [ref]abandon attempts[/ref][/m]\n" +
                    "[m2]to [ref]abandon a claim[/ref][/m]\n" +
                    "[m2]to [ref]abandon convertibility[/ref][/m]\n" +
                    "[m2]to [ref]abandon the \\[gold\\] standard[/ref][/m]\n" +
                    "[m2]to [ref]abandon \\[price\\] control[/ref][/m]\n" +
                    "[m2]to [ref]abandon a right[/ref][/m]\n", entry.getValue());
            break;
        }
    }
}