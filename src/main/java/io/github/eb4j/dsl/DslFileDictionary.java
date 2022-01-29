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

    @Override
    protected String getArticle(final DslEntry entry) throws IOException {
        byte[] buf = new byte[entry.getSize()];
        ras.seek(entry.getOffset());
        ras.readFully(buf);
        return trimArticle(buf);
    }
}
