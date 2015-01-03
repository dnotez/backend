package com.pl.dsl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.gson.reflect.TypeToken;
import com.pl.dsl.article.Article;
import com.pl.dsl.article.ArticleResult;

import java.lang.reflect.Type;

/**
 * @author mamad
 * @since 15/11/14.
 */
public class GsonHelper {
    public static final Type ARTICLE_RESULT_TYPE = new TypeToken<Result<Article>>() {
    }.getType();
    public static final Type ARTICLE_PAGED_RESPONSE_TYPE = new TypeToken<PagedResponse<ArticleResult>>() {
    }.getType();

    public static GsonHelper create() {
        return new GsonHelper();
    }

    public Gson buildDefault() {
        return new GsonBuilder()
                .registerTypeAdapter(ARTICLE_RESULT_TYPE, new ArticleResultTypeAdapter())
                .create();
    }


    private class ArticleResultTypeAdapter implements InstanceCreator<Result<Article>> {
        @Override
        public Result<Article> createInstance(Type type) {
            return new ArticleResult(0, null);
        }
    }

}
