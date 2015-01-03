package com.pl.web.extension;

import com.google.inject.Inject;
import com.pl.dsl.IdResponse;
import com.pl.dsl.article.Article;
import com.pl.store.es.ArticleStore;
import com.pl.store.es.StoreActionFailedException;
import com.pl.web.HandlerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.exec.Fulfiller;
import ratpack.func.Action;
import ratpack.handling.Chain;
import ratpack.handling.Context;

import java.net.HttpURLConnection;

import static com.pl.web.AsyncHelper.newErrorAction;
import static com.pl.web.AsyncHelper.newJsonAction;

/**
 * Handler to save stackoverflow.com answer
 *
 * @author mamad
 * @since 23/11/14.
 */
public class SOFApiHandler implements Action<Chain> {
    public static final Logger LOGGER = LoggerFactory.getLogger(SOFApiHandler.class);

    private final HandlerHelper handlerHelper;
    private final ArticleStore articleStore;

    @Inject
    public SOFApiHandler(HandlerHelper handlerHelper, ArticleStore store) {
        this.handlerHelper = handlerHelper;
        this.articleStore = store;
    }

    @Override
    public void execute(Chain chain) throws Exception {
        chain.handler("answer", this::handleSaveAnswerAsync);
    }


    public void handleSaveAnswerAsync(Context context) throws Exception {
        context.promise(new Action<Fulfiller<String>>() {
            @Override
            public void execute(Fulfiller<String> fulfiller) throws Exception {
                Article article = handlerHelper.fromBody(context, Article.class);
                try {
                    IdResponse response = articleStore.save(article);
                    handlerHelper.jsonConsumer(fulfiller).accept(response);
                } catch (StoreActionFailedException e) {
                    LOGGER.error("Store failed for SOF:{}, action:{}, id:{}, article:{}", article.getUrl(), e.getAction(), e.getId(), article, e);
                    handlerHelper.jsonConsumer(fulfiller)
                            .accept(IdResponse.create(e.getId()).error("Could not save article"));
                }
            }
        }).onError(newErrorAction(context))
                .then(newJsonAction(context, HttpURLConnection.HTTP_CREATED));
    }
}
