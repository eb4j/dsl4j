package io.github.eb4j.dsl;

import io.github.eb4j.dsl.visitor.DumpDslVisitor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DslProprietaryTest {

    private static final String SMIRNITSKY = "/content/Ru-En-Smirnitsky.dsl.dz";

    @Test
    @EnabledIf("targetFileExist")
    void loadDictionarySmirnitsky() throws URISyntaxException, IOException {
        Path dictPath = Paths.get(DslProprietaryTest.class.getResource(SMIRNITSKY).toURI());
        Path indexPath = Paths.get(dictPath + ".idx");
        DslDictionary dictionary = DslDictionary.loadDictionary(dictPath, indexPath);
        assertEquals("Smirnitsky (Ru-En)", dictionary.getDictionaryName());
        assertEquals("Russian", dictionary.getIndexLanguage());
        assertEquals("English", dictionary.getContentLanguage());
        DumpDslVisitor dumper = new DumpDslVisitor();
        Map.Entry<String, String> entry = dictionary.lookup("\u0430\u0431\u0430\u0436\u0443\u0440")
                .getEntries(dumper).get(0);
        assertEquals("\u0430\u0431\u0430\u0436\u0443\u0440", entry.getKey());
        assertEquals("[com][i][c][p]\u043C.[/p][/c][/i][/com]\n[trn][m1]lampshade, shade [/m]\n[m1][/m][/trn]\n",
                entry.getValue());
        entry = dictionary.lookup("\u044F\u0432\u043B\u044F\u0442\u044C\u0441\u044F").getEntries(dumper).get(0);
        assertTrue(entry.getValue().startsWith("[com][i]\u044F\u0432\u0438\u0442\u044C\u0441\u044F[/i][/com]\n" +
                "[trn][m1]1.  appear, present oneself; [com][i][c][p]\u043E\u0444\u0438\u0446.[/p][/c] [/i][/com]report;" +
                " ([com][i]\u043D\u0430 \u043C\u0435\u0441\u0442\u043E [c][p]\u0442\u0436.[/p][/c][/i][/com])" +
                " register (at [com][i]a[/i][/com] place);" +
                " ([com][i]\u043F\u0440\u0438\u0431\u044B\u0432\u0430\u0442\u044C[/i][/com]) arrive [/m]"));
        entry = dictionary.lookup("\u044D\u0448\u0435\u043B\u043E\u043D").getEntries(dumper).get(0);
        assertTrue(entry.getValue().startsWith("[com][i][c][p]\u043C.[/p][/c][/i][/com]\n" +
                "[trn][m1]1.  [com][i][c][p]\u0432\u043E\u0435\u043D.[/p][/c] [/i][/com]echelon [/m]"));
        try {
            Files.deleteIfExists(indexPath);
        } catch (IOException ignored) {
        }
    }

    static boolean targetFileExist() {
        return DslProprietaryTest.class.getResource(SMIRNITSKY) != null;
    }

    private static final String WORDNET = "/WordNet_3.0/En-En-WordNet3_gl_1_0.dsl.dz";

    /**
     * Test against WordNet dictionary.
     * Currently known as not working.
     * @throws URISyntaxException
     * @throws IOException
     */
    @Test
    @EnabledIf("wordnetExist")
    void loadDictionaryWordNet() throws URISyntaxException, IOException {
        Path dictPath = Paths.get(DslProprietaryTest.class.getResource(WORDNET).toURI());
        Path indexPath = Paths.get(dictPath + ".idx");
        DslDictionary dictionary = DslDictionary.loadDictionary(dictPath, indexPath);
        assertEquals("WordNet® 3.0 (En-En)", dictionary.getDictionaryName());
        assertEquals("English", dictionary.getIndexLanguage());
        assertEquals("English", dictionary.getContentLanguage());
        DumpDslVisitor dumper = new DumpDslVisitor();
        Map.Entry<String, String> entry = dictionary.lookup("zodiac").getEntries(dumper).get(0);
        assertEquals("zodiac", entry.getKey());
        assertTrue(entry.getValue().startsWith("[m1][p]noun[/p][/m]\n" +
                        "[m2][b]1.[/b] [trn]a belt-shaped region in the heavens on either side to the ecliptic[/trn];" +
                        " [trn]divided into 12 constellations or signs for astrological purposes[/trn][/m]\n" +
                        "[m3][com][c dodgerblue]•[/c] [p]Derivationally related forms[/p]: ↑<<zodiacal>>[/com][/m]\n"));
        try {
            Files.deleteIfExists(indexPath);
        } catch (IOException ignored) {
        }
    }

    static boolean wordnetExist() {
        return DslProprietaryTest.class.getResource(WORDNET) != null;
    }

    private static final String APRESYAN = "/content/En-Ru-Apresyan.dsl.dz";

    @Test
    @EnabledIf("apresyanExist")
    void loadDictionaryApresyan() throws URISyntaxException, IOException {
        Path dictPath = Paths.get(DslProprietaryTest.class.getResource(APRESYAN2).toURI());
        Path indexPath = Paths.get(dictPath + ".idx");
        DslDictionary dictionary = DslDictionary.loadDictionary(dictPath, indexPath);
        assertEquals("Apresyan (En-Ru)", dictionary.getDictionaryName());
        assertEquals("English", dictionary.getIndexLanguage());
        assertEquals("Russian", dictionary.getContentLanguage());
        DumpDslVisitor dumper = new DumpDslVisitor();
        Map.Entry<String, String> entry = dictionary.lookupPredictive("zombi")
                .getEntries(dumper).get(0);
        assertEquals("zombi{(}e{)}", entry.getKey());
        assertEquals("[m0\\]\\[[t]ˈzɒmbɪ[/t]\\] [p]n[/p][/m]\n"
                + "[m1]1. 1) [trn]зомби, оживший мертвец; оборотень[/trn][/m]\n"
                + "[m3]2) [trn]колдовство; нечистая сила[/trn][/m]\n" + "[m1]2. [p]сл.[/p] [trn]зануда, тупица, кретин[/trn][/m]\n"
                + "[m1]3. [p]сл.[/p] [trn]чудной, малахольный тип[/trn][/m]\n"
                + "[m1]4. [trn]коктейль из рома с фруктовым соком и содовой водой[/trn][/m]\n"
                + "[m1]5. [p]воен.[/p] [p]жарг.[/p] [trn]новобранец[/trn][/m]\n"
                + "[m1]6. [p]рел.[/p], [p]фольк.[/p] [trn]божество-змея; священный питон[/trn]"
                + " [com]([i]в Вест-Индии, на юге США[/i] [p]и т. п.[/p])[/com][/m]\n",
                entry.getValue());
        try {
            Files.deleteIfExists(indexPath);
        } catch (IOException ignored) {
        }
    }

    static boolean apresyanExist() {
        return DslProprietaryTest.class.getResource(APRESYAN) != null;
    }

    private static final String APRESYAN2 = "/En-Ru_Apresyan/En-Ru_Apresyan.dsl.dz";
}
