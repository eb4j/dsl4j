package io.github.eb4j.dsl;

import io.github.eb4j.dsl.visitor.DumpDslVisitor;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DslDictionaryIndexTest {

    private final URL resource = this.getClass().getResource("/test1.dsl.dz");
    private final URL resource2 = this.getClass().getResource("/test4.dsl.dz");

    @Test
    @Order(1)
    void saveDicitonaryIndex() throws URISyntaxException, IOException {
        Path dictPath = Paths.get(resource.toURI());
        Path indexPath = Paths.get(dictPath + ".idx");
        DslDictionary dictionary = DslDictionary.loadDictionary(dictPath, indexPath);
        assertEquals("Test (En-Ru)", dictionary.getDictionaryName());
        assertEquals("English", dictionary.getIndexLanguage());
        assertEquals("Russian", dictionary.getContentLanguage());
        DumpDslVisitor dumper = new DumpDslVisitor();
        Map.Entry<String, String> entry = dictionary.lookup("space").getEntries(dumper).get(0);
        assertEquals("space", entry.getKey());
        assertEquals("[m1][trn]Only a single white space on first character[/trn][/m]\n",
                entry.getValue());
        entry = dictionary.lookup("tab").getEntries(dumper).get(0);
        assertEquals("tab", entry.getKey());
        assertEquals("[m1][trn]Translation line also can have a single TAB char[/trn][/m]\n",
                entry.getValue());
        entry = dictionary.lookup("tag").getEntries(dumper).get(0);
        assertEquals("tag", entry.getKey());
        assertEquals("[m1][trn]tag should be ignored[/trn][/m]\n", entry.getValue());
        dumper = new DumpDslVisitor();
        entry = dictionary.lookup("abandon").getEntries(dumper).get(0);
            assertEquals("[m1][b]1.[/b] [trn]отказываться [com]([i]от чего-л.[/i])[/com]," +
                    " прекращать [com]([i]попытки и т. п.[/i])[/com][/trn][/m]\n" +
                    "[m1][b]2.[/b] [trn]покидать, оставлять[/trn][/m]\n" +
                    "[m2]to [ref]abandon attempts[/ref][/m]\n" +
                    "[m2]to [ref]abandon a claim[/ref][/m]\n" +
                    "[m2]to [ref]abandon convertibility[/ref][/m]\n" +
                    "[m2]to [ref]abandon the \\[gold\\] standard[/ref][/m]\n" +
                    "[m2]to [ref]abandon \\[price\\] control[/ref][/m]\n" +
                    "[m2]to [ref]abandon a right[/ref][/m]\n", entry.getValue());
    }

    @Test
    @Order(2)
    void loadDicitonaryIndex() throws URISyntaxException, IOException {
        Path dictPath = Paths.get(resource.toURI());
        Path indexPath = Paths.get(dictPath + ".idx");
        Assumptions.assumeTrue(Files.exists(indexPath) && indexPath.toFile().canRead());
        DslDictionary dictionary = DslDictionary.loadDictionary(dictPath, indexPath);
        assertEquals("Test (En-Ru)", dictionary.getDictionaryName());
        assertEquals("English", dictionary.getIndexLanguage());
        assertEquals("Russian", dictionary.getContentLanguage());
        DumpDslVisitor dumper = new DumpDslVisitor();
        Map.Entry<String, String> entry = dictionary.lookup("space").getEntries(dumper).get(0);
        assertEquals("space", entry.getKey());
        assertEquals("[m1][trn]Only a single white space on first character[/trn][/m]\n",
                entry.getValue());

        entry = dictionary.lookup("tab").getEntries(dumper).get(0);
        assertEquals("tab", entry.getKey());
        assertEquals("[m1][trn]Translation line also can have a single TAB char[/trn][/m]\n",
                entry.getValue());
        entry = dictionary.lookup("tag").getEntries(dumper).get(0);
        assertEquals("tag", entry.getKey());
        assertEquals("[m1][trn]tag should be ignored[/trn][/m]\n", entry.getValue());
        dumper = new DumpDslVisitor();
        entry = dictionary.lookup("abandon").getEntries(dumper).get(0);
        assertEquals("[m1][b]1.[/b] [trn]отказываться [com]([i]от чего-л.[/i])[/com]," +
                " прекращать [com]([i]попытки и т. п.[/i])[/com][/trn][/m]\n" +
                "[m1][b]2.[/b] [trn]покидать, оставлять[/trn][/m]\n" +
                "[m2]to [ref]abandon attempts[/ref][/m]\n" +
                "[m2]to [ref]abandon a claim[/ref][/m]\n" +
                "[m2]to [ref]abandon convertibility[/ref][/m]\n" +
                "[m2]to [ref]abandon the \\[gold\\] standard[/ref][/m]\n" +
                "[m2]to [ref]abandon \\[price\\] control[/ref][/m]\n" +
                "[m2]to [ref]abandon a right[/ref][/m]\n", entry.getValue());
    }

    @Test
    void loadDicitonaryIndex2() throws URISyntaxException, IOException {
        Path dictPath = Paths.get(resource2.toURI());
        DslDictionary dictionary = DslDictionary.loadDictionary(dictPath, null);
        assertEquals("IPA Dictionary - English", dictionary.getDictionaryName());
        assertEquals("English", dictionary.getIndexLanguage());
        assertEquals("English", dictionary.getContentLanguage());
        DumpDslVisitor dumper = new DumpDslVisitor();
        Map.Entry<String, String> entry = dictionary.lookup("ace").getEntries(dumper).get(0);
        assertEquals("ace", entry.getKey());
        assertEquals("[m1]/ˈeɪs/[/m]\n", entry.getValue());
        entry = dictionary.lookup("aerogenosa").getEntries(dumper).get(0);
        assertEquals("[m1]/ˈɛɹədʒəˌnoʊsə/[/m]\n", entry.getValue());
        entry = dictionary.lookup("agree").getEntries(dumper).get(0);
        assertEquals("[m1]/əˈɡɹi/[/m]\n", entry.getValue());
        entry = dictionary.lookup("ahren").getEntries(dumper).get(0);
        assertEquals("[m1]/ˈɑɹən/[/m]\n", entry.getValue());
        entry = dictionary.lookup("aiwa").getEntries(dumper).get(0);
        assertEquals("[m1]/ˈaɪwə/[/m]\n", entry.getValue());
        entry = dictionary.lookup("analysis").getEntries(dumper).get(0);
        assertEquals("[m1]/əˈnæɫəsəs/, /əˈnæɫɪsɪs/[/m]\n", entry.getValue());
    }
}
