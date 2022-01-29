package io.github.eb4j.dsl;

import io.github.eb4j.dsl.data.DictionaryData;
import io.github.eb4j.dsl.data.DslDictionaryProperty;
import io.github.eb4j.dsl.data.DslEntry;
import org.dict.zip.DictZipInputStream;
import org.dict.zip.RandomAccessInputStream;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;

public class DslZipDictionary extends DslDictionary {

    private final DictZipInputStream dzis;

    public DslZipDictionary(final Path path, final DictionaryData<DslEntry> dictionaryData,
                            final DslDictionaryProperty prop) throws IOException {
        super(dictionaryData, prop);
        dzis = new DictZipInputStream(
                new RandomAccessInputStream(new RandomAccessFile(path.toFile(), "r")));
    }

    @Override
    protected String getArticle(final DslEntry entry) throws IOException {
        byte[] buf = new byte[entry.getSize()];
        dzis.seek(entry.getOffset());
        dzis.readFully(buf);
        return trimArticle(buf);
    }
}
