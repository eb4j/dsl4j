package io.github.eb4j.dsl;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DslResult {

    private final List<Map.Entry<String, Object>> result;

    public DslResult(List<Map.Entry<String, Object>> res) {
        result = res;
    }

    public <T> List<Map.Entry<String, T>> getText(final AbstractDslVisitor<T> filter) {
        List<Map.Entry<String, T>> res = new ArrayList<>();
        for (Map.Entry<String, Object> entry: result) {
            try {
                DslParser parser = DslParser.createParser((String) entry.getValue());
                DslArticle article = parser.DslArticle();
                article.accept(filter);
                res.add(new AbstractMap.SimpleImmutableEntry<>(entry.getKey(), filter.getObject()));
            } catch (ParseException e) {
            }
        }
        return res;
    }

}
