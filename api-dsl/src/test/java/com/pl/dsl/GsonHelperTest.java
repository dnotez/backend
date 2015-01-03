package com.pl.dsl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pl.dsl.article.Article;
import com.pl.dsl.article.ArticleResult;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author mamad
 * @since 15/11/14.
 */
public class GsonHelperTest {
    @Test
    public void testDeserializePagedResponse() throws Exception {
        Gson gson = GsonHelper.create().buildDefault();
        ArticleResult result = new ArticleResult(1.2f, new Article("12", "u1", "t1", "b1"));
        String json = gson.toJson(result);
        assertTrue(json, json.contains("score"));
        List<ArticleResult> results = Arrays.asList(result);
        PagedResponse<ArticleResult> response = new PagedResponse<>(results, 1);
        json = gson.toJson(response, new TypeToken<PagedResponse<Article>>() {
        }.getType());
        assertTrue(json, json.contains("score"));

        PagedResponse<ArticleResult> loaded = gson.fromJson(json, GsonHelper.ARTICLE_PAGED_RESPONSE_TYPE);
        assertNotNull(loaded);
        assertNotNull(loaded.getResults());
        assertEquals(1, loaded.getResults().size());
        ArticleResult r = loaded.getResults().get(0);
        assertNotNull(r);
        assertNotNull(r.getItem());
        assertEquals(result.getScore(), r.getScore(), 0);
        assertEquals(result.getItem().getId(), r.getItem().getId());
    }

}
