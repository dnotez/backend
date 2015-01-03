package com.pl;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pl.dsl.article.Article;
import com.pl.dsl.article.ArticleFields;
import com.pl.store.es.ArticleStore;
import org.junit.Assume;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import static org.junit.Assume.*;

/**
 * Helper class to test some articles.
 *
 * @author mamad
 * @since 29/11/14.
 */
public class EsTestDataIndexer implements BulkIndexer {


    public static EsTestDataIndexer create() {
        return new EsTestDataIndexer();
    }

    @SuppressWarnings("unchecked")
    public int indexStackoverflow(Consumer<Article> answerConsumer) {
        int count = 0;
        try (InputStream sofJsonStream = this.getClass().getResourceAsStream("/stackoverflow_answers.json")) {
            assumeNotNull(sofJsonStream);
            Gson gson = new GsonBuilder().create();
            Map sof = gson.fromJson(new InputStreamReader(sofJsonStream, Charsets.UTF_8), Map.class);
            assumeTrue(sof.containsKey("stackoverflow"));
            assumeTrue(sof.get("stackoverflow") instanceof List);
            List<Map> answers = (List<Map>) sof.get("stackoverflow");
            assumeFalse(answers.isEmpty());
            for (Map answer : answers) {
                answerConsumer.accept(toArticle(answer));
                count++;
            }
        } catch (IOException e) {
            Assume.assumeNoException("IO Error in reading stackoverflow samples json", e);
        }
        return count;
    }

    private Article toArticle(Map map) {
        assumeTrue(map.containsKey(ArticleFields.TITLE));
        assumeTrue(map.containsKey(ArticleFields.URL));
        assumeTrue(map.containsKey(ArticleFields.BODY));
        return new Article(UUID.randomUUID().toString(), map.get(ArticleFields.URL).toString(),
                map.get(ArticleFields.TITLE).toString(), map.get(ArticleFields.BODY).toString());
    }

    @Override
    public int index(ArticleStore store) {
        return indexStackoverflow(answer -> {
            try {
                store.save(answer);
            } catch (Exception e) {
                Assume.assumeNoException(e);
            }
        });
    }
}
