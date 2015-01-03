package com.pl.web.api.article;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.pl.dsl.IdResponse;
import com.pl.dsl.PagedRequest;
import com.pl.dsl.article.Article;
import com.pl.store.es.ArticleStore;
import com.pl.store.es.StoreActionFailedException;
import com.pl.web.AsyncHelper;
import com.pl.web.HandlerHelper;
import com.pl.web.StatusHelper;
import com.pl.web.api.DAOHandler;
import ratpack.exec.Fulfiller;
import ratpack.func.Action;
import ratpack.handling.Context;

import java.io.IOException;

/**
 * @author mamad
 * @since 13/11/14.
 */
@Singleton
public class ArticleHandler extends DAOHandler<Article> {

    private final ArticleStore articleStore;

    @Inject
    public ArticleHandler(ArticleStore articleStore, HandlerHelper handlerHelper) {
        super(Article.class, handlerHelper);
        this.articleStore = articleStore;
    }

    @Override
    protected Optional<Article> findById(String id) throws IOException, StoreActionFailedException {
        return articleStore.findById(id);
    }

    @Override
    protected IdResponse save(Article object) throws IOException, StoreActionFailedException {
        return articleStore.save(object);
    }

    @Override
    protected IdResponse update(Article object) {
        return articleStore.update(object);
    }

    @Override
    protected IdResponse delete(String id) {
        return articleStore.delete(id);
    }

    @Override
    public void asyncList(PagedRequest request, Context context) {
        context.promise(new Action<Fulfiller<String>>() {
            @Override
            public void execute(Fulfiller<String> fulfiller) throws Exception {
                articleStore.asyncList(request, handlerHelper.jsonConsumer(fulfiller), fulfiller::error);
            }
        }).onError(new Action<Throwable>() {
                       @Override
                       public void execute(Throwable throwable) throws Exception {
                           StatusHelper.sendInternalError(context, throwable);
                       }
                   }
        ).then(AsyncHelper.newJsonAction(context));
    }
}
