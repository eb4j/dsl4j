package io.github.eb4j.dsl;

import io.github.eb4j.dsl.data.DictionaryData;
import io.github.eb4j.dsl.data.DslDictionaryProperty;
import io.github.eb4j.dsl.data.DslEntry;
import org.dict.zip.RandomAccessInputStream;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;


public class DslFileDictionary extends DslDictionary {

    private final RandomAccessInputStream ras;

    public DslFileDictionary(final Path path, final DictionaryData<DslEntry> dictionaryData,
                             final DslDictionaryProperty prop) throws IOException {
        super(dictionaryData, prop);
        ras = new RandomAccessInputStream(new RandomAccessFile(path.toFile(), "r"));
    }

    protected String getArticle(final DslEntry entry) throws IOException {
        long offset = entry.getOffset();
        int size = entry.getSize();
        byte[] buf = new byte[size];
        ras.seek(offset);
        ras.readFully(buf);
        String[] tokens = new String(buf, prop.getCharset()).split("\\r?\\n");
        StringBuilder article = new StringBuilder();
        for (String token: tokens) {
            int i = 0;
            while (i < token.length() && (token.charAt(i) == '\t' || token.charAt(i) == ' ')) {
                i++;
            }
            article.append(token.substring(i)).append("\n");
        }
        return article.toString();
    }
}
