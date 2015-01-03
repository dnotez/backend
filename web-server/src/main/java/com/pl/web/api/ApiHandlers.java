package com.pl.web.api;

import com.pl.web.api.article.ArticleCollectionHandler;
import com.pl.web.api.article.ArticleHandler;
import com.pl.web.api.bookmark.BookmarkHandler;
import com.pl.web.api.category.CategoryHandler;
import com.pl.web.api.health.PingHandler;
import ratpack.func.Action;
import ratpack.handling.Chain;

/**
 * @author mamad
 * @since 10/11/14.
 */
public class ApiHandlers implements Action<Chain> {

    @Override
    public void execute(Chain chain) throws Exception {
        chain
                .handler("ping", chain.getRegistry().get(PingHandler.class))
                .prefix("category", chain.getRegistry().get(CategoryHandler.class))
                .prefix("bookmark", chain.getRegistry().get(BookmarkHandler.class))
                .prefix("articles", chain.getRegistry().get(ArticleCollectionHandler.class))
                .prefix("article", chain.getRegistry().get(ArticleHandler.class));
    }
}
