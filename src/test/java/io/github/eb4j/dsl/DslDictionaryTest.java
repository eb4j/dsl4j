package io.github.eb4j.dsl;

import io.github.eb4j.dsl.visitor.DumpDslVisitor;
import io.github.eb4j.dsl.visitor.HtmlDslVisitor;
import io.github.eb4j.dsl.visitor.PlainDslVisitor;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DslDictionaryTest {

    private final URL resource = this.getClass().getResource("/utf16le_bom_crlf_el.dsl");

    @Test
    void loadDictionarySingle() throws URISyntaxException, IOException {
        DslDictionary dictionary = DslDictionary.loadDictionary(new File(resource.toURI()));
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
    }

    @Test
    void loadDictionaryPredictive() throws URISyntaxException, IOException {
        DslDictionary dictionary = DslDictionary.loadDictionary(new File(resource.toURI()));
        DumpDslVisitor dumper = new DumpDslVisitor();
        DslResult results = dictionary.lookupPredictive("spa");
        Map.Entry<String, String> entry = results.getEntries(dumper).get(0);
        assertEquals("space", entry.getKey());
        assertEquals("[m1][trn]Only a single white space on first character[/trn][/m]\n",
                entry.getValue());
        entry = dictionary.lookupPredictive("ta").getEntries(dumper).get(0);
        assertEquals("tab", entry.getKey());
        assertEquals("[m1][trn]Translation line also can have a single TAB char[/trn][/m]\n",
                entry.getValue());
    }

    @Test
    void loadDictionaryMulti() throws URISyntaxException, IOException {
        DslDictionary dictionary = DslDictionary.loadDictionary(new File(resource.toURI()));
        DslResult res = dictionary.lookup("abandon");

        PlainDslVisitor plainFilter = new PlainDslVisitor();
        Map.Entry<String, String> entry = res.getEntries(plainFilter).get(0);
        assertEquals("1. отказываться (от чего-л.)," +
                " прекращать (попытки и т. п.)\n" +
                "2. покидать, оставлять\n" +
                "to abandon attempts\n" +
                "to abandon a claim\n" +
                "to abandon convertibility\n" +
                "to abandon the [gold] standard\n" +
                "to abandon [price] control\n" +
                "to abandon a right\n", entry.getValue());

        DumpDslVisitor dumper = new DumpDslVisitor();
        entry = res.getEntries(dumper).get(0);
        assertEquals("[m1][b]1.[/b] [trn]отказываться [com]([i]от чего-л.[/i])[/com]," +
                " прекращать [com]([i]попытки и т. п.[/i])[/com][/trn][/m]\n" +
                "[m1][b]2.[/b] [trn]покидать, оставлять[/trn][/m]\n" +
                "[m2]to [ref]abandon attempts[/ref][/m]\n" +
                "[m2]to [ref]abandon a claim[/ref][/m]\n" +
                "[m2]to [ref]abandon convertibility[/ref][/m]\n" +
                "[m2]to [ref]abandon the \\[gold\\] standard[/ref][/m]\n" +
                "[m2]to [ref]abandon \\[price\\] control[/ref][/m]\n" +
                "[m2]to [ref]abandon a right[/ref][/m]\n", entry.getValue());

        HtmlDslVisitor filter = new HtmlDslVisitor();
        entry = res.getEntries(filter).get(0);
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
                "<p style=\"text-indent: 60px\">to abandon [price] control</p>\n" +
                "<p style=\"text-indent: 60px\">to abandon a right</p>\n", entry.getValue());
    }

    @Test
    void loadDictionaryMedia() throws URISyntaxException, IOException {
        DslDictionary dictionary = DslDictionary.loadDictionary(new File(resource.toURI()));
        DslResult res = dictionary.lookup("media");
        File current = new File(".");
        HtmlDslVisitor filter = new HtmlDslVisitor(current.getPath());
        Map.Entry entry = res.getEntries(filter).get(0);
        assertEquals("<p>this is media <img src=\"file:"
                + new File(current, "image.jpg").getAbsolutePath()
                + "\" />  image and <a href=\"file:"
                + new File(current, "video.ogv").getAbsolutePath()
                + "\">video.ogv</a></p>\n", entry.getValue());
    }

    @Test
    void loadCp1251_CRLF() throws URISyntaxException, IOException {
        URL cp1251 = this.getClass().getResource("/cp1251_crlf.dsl");
        DslDictionary dictionary = DslDictionary.loadDictionary(new File(cp1251.toURI()));
        assertEquals("Test (En-Ru)", dictionary.getDictionaryName());
        assertEquals("English", dictionary.getIndexLanguage());
        assertEquals("Russian", dictionary.getContentLanguage());
        DumpDslVisitor dumper = new DumpDslVisitor();
        DslResult results = dictionary.lookup("test");
        Map.Entry<String, String> entry = results.getEntries(dumper).get(0);
        assertEquals("[m1][trn]контрольная работа[/trn][/m]\n",
                entry.getValue());
    }

    @Test
    void loadUTF16LF_BOM_LF_EL() throws URISyntaxException, IOException {
        URL utf16lelf = this.getClass().getResource("/utf16le_bom_lf_el.dsl");
        DslDictionary dictionary = DslDictionary.loadDictionary(new File(utf16lelf.toURI()));
        assertEquals("en-ja Wikidict", dictionary.getDictionaryName());
        assertEquals("English", dictionary.getIndexLanguage());
        assertEquals("Japanese", dictionary.getContentLanguage());
        DumpDslVisitor dumper = new DumpDslVisitor();
        DslResult results = dictionary.lookup("Japan");
        Map.Entry<String, String> entry = results.getEntries(dumper).get(0);
        assertEquals("[m1]Japan[/m]\n[m1]日本[/m]\n", entry.getValue());
    }

    @Test
    void loadUTF16_End_DoubleEol() throws URISyntaxException, IOException {
        URL utf16lelf = this.getClass().getResource("/utf16_double_eol.dsl.dz");
        DslDictionary dictionary = DslDictionary.loadDictionary(new File(utf16lelf.toURI()));
        assertEquals("en-ja Wikidict", dictionary.getDictionaryName());
        assertEquals("English", dictionary.getIndexLanguage());
        assertEquals("Japanese", dictionary.getContentLanguage());
        DumpDslVisitor dumper = new DumpDslVisitor();
        DslResult results = dictionary.lookup("Voiceless palatal stop");
        Map.Entry<String, String> entry = results.getEntries(dumper).get(0);
        assertEquals("[m1]Voiceless palatal stop[/m]\n[m1]無声硬口蓋破裂音[/m]\n", entry.getValue());
    }

    @Test
    void loadUtf8_LF_NOEL() throws URISyntaxException, IOException {
        URL utf8 = this.getClass().getResource("/utf8_lf_noel.dsl");
        DslDictionary dictionary = DslDictionary.loadDictionary(new File(utf8.toURI()));
        assertEquals("UTF-8 dictionary", dictionary.getDictionaryName());
        assertEquals("English", dictionary.getIndexLanguage());
        assertEquals("Japanese", dictionary.getContentLanguage());
        DumpDslVisitor dumper = new DumpDslVisitor();
        DslResult results = dictionary.lookup("Life");
        Map.Entry<String, String> entry = results.getEntries(dumper).get(0);
        assertEquals("[m1]Life[/m]\n[m1]\u751F\u547D[/m]\n", entry.getValue());
    }

    @Test
    void loadUtf8_LF_EL() throws URISyntaxException, IOException {
        URL utf8 = this.getClass().getResource("/utf8_lf_el.dsl");
        DslDictionary dictionary = DslDictionary.loadDictionary(new File(utf8.toURI()));
        assertEquals("UTF-8 dictionary", dictionary.getDictionaryName());
        assertEquals("English", dictionary.getIndexLanguage());
        assertEquals("Japanese", dictionary.getContentLanguage());
        DumpDslVisitor dumper = new DumpDslVisitor();
        DslResult results = dictionary.lookup("Life");
        Map.Entry<String, String> entry = results.getEntries(dumper).get(0);
        assertEquals("[m1]Life[/m]\n[m1]\u751F\u547D[/m]\n", entry.getValue());
    }

    @Test
    void loadUtf8_BOM_LF_EL() throws URISyntaxException, IOException {
        URL utf8 = this.getClass().getResource("/utf8_bom_lf_el.dsl");
        DslDictionary dictionary = DslDictionary.loadDictionary(new File(utf8.toURI()));
        assertEquals("test (En-Ru)", dictionary.getDictionaryName());
        assertEquals("English", dictionary.getIndexLanguage());
        assertEquals("Russian", dictionary.getContentLanguage());
        DumpDslVisitor dumper = new DumpDslVisitor();
        DslResult results = dictionary.lookup("ABC");
        Map.Entry<String, String> entry = results.getEntries(dumper).get(0);
        assertEquals("[m1]\\[[t]ABC[/t]\\] [p]n[/p][/m]\n", entry.getValue());
        results = dictionary.lookup("Foo");
        entry = results.getEntries(dumper).get(0);
        assertEquals("[m1]\\[[t]Boo[/t]\\] [p]pl[/p][/m]\n", entry.getValue());
    }

    @Test
    void loadUtf8_BOM_LF_NOEL() throws URISyntaxException, IOException {
        URL utf8 = this.getClass().getResource("/utf8_bom_lf_noel.dsl");
        DslDictionary dictionary = DslDictionary.loadDictionary(new File(utf8.toURI()));
        assertEquals("test (En-Ru)", dictionary.getDictionaryName());
        assertEquals("English", dictionary.getIndexLanguage());
        assertEquals("Russian", dictionary.getContentLanguage());
        DumpDslVisitor dumper = new DumpDslVisitor();
        DslResult results = dictionary.lookup("ABC");
        Map.Entry<String, String> entry = results.getEntries(dumper).get(0);
        assertEquals("[m1]\\[[t]ABC[/t]\\] [p]n[/p][/m]\n", entry.getValue());
        results = dictionary.lookup("Foo");
        entry = results.getEntries(dumper).get(0);
        assertEquals("[m1]\\[[t]Boo[/t]\\] [p]pl[/p][/m]\n", entry.getValue());
    }

    @Test
    void loadUTF16LE_BOM_LF_NOEL() throws URISyntaxException, IOException {
        URL utf16le_lf_nel = this.getClass().getResource("/utf16le_lf_nel.dsl");
        Path dictPath = Paths.get(utf16le_lf_nel.toURI());
        DslDictionary dictionary = DslDictionary.loadDictionary(dictPath, null);
        DumpDslVisitor dumper = new DumpDslVisitor();
        Map.Entry<String, String> entry = dictionary.lookup("ace").getEntries(dumper).get(0);
        assertEquals("ace", entry.getKey());
        assertEquals("[m1]ace[/m]\n", entry.getValue());
        entry = dictionary.lookup("Universe").getEntries(dumper).get(0);
        assertEquals("Universe", entry.getKey());
        assertEquals("[m1]\u5B87\u5B99[/m]\n", entry.getValue());
    }

    @Test
    void loadUTF16LE_NOBOM_LF_EL_DZ() throws URISyntaxException, IOException {
        URL resource2 = this.getClass().getResource("/utf16le_nobom_lf_el.dsl.dz");
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

    @Test
    void loadUTF16LE_BOM_CRLF_EL_DZ() throws URISyntaxException, IOException {
        Path dictPath = Paths.get(this.getClass().getResource("/utf16le_bom_crlf_el.dsl.dz").toURI());
        DslDictionary dictionary = DslDictionary.loadDictionary(dictPath);
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
}
