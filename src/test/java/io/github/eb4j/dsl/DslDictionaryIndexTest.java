package io.github.eb4j.dsl;

import io.github.eb4j.dsl.visitor.DumpDslVisitor;
import org.junit.AfterClass;
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

public class DslDictionaryIndexTest {

    private static final URL RESOURCE = DslDictionaryIndexTest.class.getResource("/utf16le_bom_crlf_el.dsl");

    /**
     * Clean up created index cache file.
     * @throws IOException
     * @throws URISyntaxException
     */
    @AfterClass
    public static void cleanIndex() throws IOException, URISyntaxException {
        Path dictPath = Paths.get(RESOURCE.toURI());
        Path indexPath = Paths.get(dictPath + ".idx");
        Files.deleteIfExists(indexPath);
    }

    /**
     * Load dictionary and save index cache.
     * @throws URISyntaxException
     * @throws IOException
     */
    @Test
    @Order(1)
    public void saveDictionaryIndex() throws URISyntaxException, IOException {
        Path dictPath = Paths.get(RESOURCE.toURI());
        Path indexPath = Paths.get(dictPath + ".idx");
        DslDictionary dictionary = DslDictionary.loadDictionary(dictPath, indexPath);
        assertEquals("Test (En-Ru)", dictionary.getDictionaryName());
        assertEquals("English", dictionary.getIndexLanguage());
        assertEquals("Russian", dictionary.getContentLanguage());
        DumpDslVisitor dumper = new DumpDslVisitor();
        Map.Entry<String, String> entry = dictionary.lookup("space").getEntries(dumper).get(0);
        assertEquals("space", entry.getKey());
        assertEquals("[m1][trn]Only a single white space on first character[/trn][/m]", entry.getValue());
        entry = dictionary.lookup("tab").getEntries(dumper).get(0);
        assertEquals("tab", entry.getKey());
        assertEquals("[m1][trn]Translation line also can have a single TAB char[/trn][/m]", entry.getValue());
        entry = dictionary.lookup("tag").getEntries(dumper).get(0);
        assertEquals("tag", entry.getKey());
        assertEquals("[m1][trn]tag should be ignored[/trn][/m]", entry.getValue());
        dumper = new DumpDslVisitor();
        entry = dictionary.lookup("abandon").getEntries(dumper).get(0);
            assertEquals("[m1][b]1.[/b] [trn]???????????????????????? [com]([i]???? ????????-??.[/i])[/com]," +
                    " ???????????????????? [com]([i]?????????????? ?? ??. ??.[/i])[/com][/trn][/m]\n" +
                    "[m1][b]2.[/b] [trn]????????????????, ??????????????????[/trn][/m]\n" +
                    "[m2]to [ref]abandon attempts[/ref][/m]\n" +
                    "[m2]to [ref]abandon a claim[/ref][/m]\n" +
                    "[m2]to [ref]abandon convertibility[/ref][/m]\n" +
                    "[m2]to [ref]abandon the \\[gold\\] standard[/ref][/m]\n" +
                    "[m2]to [ref]abandon \\[price\\] control[/ref][/m]\n" +
                    "[m2]to [ref]abandon a right[/ref][/m]", entry.getValue());
    }

    /**
     * Load index file which saveDictionaryIndex method created.
     * @throws URISyntaxException
     * @throws IOException
     */
    @Test
    @Order(2)
    public void loadDictionaryIndex() throws URISyntaxException, IOException {
        Path dictPath = Paths.get(RESOURCE.toURI());
        Path indexPath = Paths.get(dictPath + ".idx");
        Assumptions.assumeTrue(Files.exists(indexPath) && indexPath.toFile().canRead());
        DslDictionary dictionary = DslDictionary.loadDictionary(dictPath, indexPath);
        assertEquals("Test (En-Ru)", dictionary.getDictionaryName());
        assertEquals("English", dictionary.getIndexLanguage());
        assertEquals("Russian", dictionary.getContentLanguage());
        DumpDslVisitor dumper = new DumpDslVisitor();
        Map.Entry<String, String> entry = dictionary.lookup("space").getEntries(dumper).get(0);
        assertEquals("space", entry.getKey());
        assertEquals("[m1][trn]Only a single white space on first character[/trn][/m]", entry.getValue());
        entry = dictionary.lookup("tab").getEntries(dumper).get(0);
        assertEquals("tab", entry.getKey());
        assertEquals("[m1][trn]Translation line also can have a single TAB char[/trn][/m]", entry.getValue());
        entry = dictionary.lookup("tag").getEntries(dumper).get(0);
        assertEquals("tag", entry.getKey());
        assertEquals("[m1][trn]tag should be ignored[/trn][/m]", entry.getValue());
        dumper = new DumpDslVisitor();
        entry = dictionary.lookup("abandon").getEntries(dumper).get(0);
        assertEquals("[m1][b]1.[/b] [trn]???????????????????????? [com]([i]???? ????????-??.[/i])[/com]," +
                " ???????????????????? [com]([i]?????????????? ?? ??. ??.[/i])[/com][/trn][/m]\n" +
                "[m1][b]2.[/b] [trn]????????????????, ??????????????????[/trn][/m]\n" +
                "[m2]to [ref]abandon attempts[/ref][/m]\n" +
                "[m2]to [ref]abandon a claim[/ref][/m]\n" +
                "[m2]to [ref]abandon convertibility[/ref][/m]\n" +
                "[m2]to [ref]abandon the \\[gold\\] standard[/ref][/m]\n" +
                "[m2]to [ref]abandon \\[price\\] control[/ref][/m]\n" +
                "[m2]to [ref]abandon a right[/ref][/m]", entry.getValue());
    }
}
