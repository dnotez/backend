package com.pl.store.es;

import com.pl.dsl.article.Article;

import java.util.Map;

/**
 * This composer is used to compose completion field ArticleFields.SUGGEST.
 * Use the title as input and id of the article as the payload
 *
 * @author mamad
 * @since 30/11/14.
 */
public interface ArticleSuggestionComposer {
    Map<String, Object> createSuggestionObject(Article article);
}
