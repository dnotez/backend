package com.pl.store.es;

import com.pl.dsl.article.Article;

/**
 * @author mamad
 * @since 13/12/14.
 */
public class DuplicateArticleException extends Throwable {
    private final Article article;

    public DuplicateArticleException(Article article) {
        this.article = article;
    }

    public Article getArticle() {
        return article;
    }
}
