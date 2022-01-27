package io.github.eb4j.dsl;

import io.github.eb4j.dsl.data.DictionaryData;
import io.github.eb4j.dsl.data.DslEntry;
import org.dict.zip.DictZipInputStream;
import org.dict.zip.RandomAccessInputStream;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;

public class DslZipDictionary extends DslDictionary {

    private final DictZipInputStream dictZipInputStream;

    public DslZipDictionary(final Path path, final DictionaryData<DslEntry> dictionaryData,
                            final DslDictionaryProperty prop) throws IOException {
        super(dictionaryData, prop);
        dictZipInputStream = new DictZipInputStream(
                new RandomAccessInputStream(new RandomAccessFile(path.toFile(), "r")));
    }

    @Override
    protected String getArticle(final DslEntry entry) throws IOException {
        long offset = entry.getOffset();
        int size = entry.getSize();
        byte[] buf = new byte[size];
        dictZipInputStream.seek(offset);
        dictZipInputStream.readFully(buf);
        String article = new String(buf, prop.getCharset());
        int i = 0;
        while (article.charAt(i) == '\t' || article.charAt(i) == ' ') {
            i++;
        }
        return article.substring(i);
    }
}
