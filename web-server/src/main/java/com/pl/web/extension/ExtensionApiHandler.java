package com.pl.web.extension;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.pl.dsl.IdResponse;
import com.pl.dsl.PagedRequest;
import com.pl.dsl.article.Article;
import com.pl.dsl.extension.GetByUrlRequest;
import com.pl.fetch.ArticleBodyFetcher;
import com.pl.fetch.FetchResponse;
import com.pl.store.es.ArticleStore;
import com.pl.store.es.StoreActionFailedException;
import com.pl.web.HandlerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.exec.Fulfiller;
import ratpack.func.Action;
import ratpack.handling.Chain;
import ratpack.handling.Context;

import static com.pl.web.AsyncHelper.newErrorAction;
import static com.pl.web.AsyncHelper.newJsonAction;

/**
 * @author mamad
 * @since 23/11/14.
 */
public class ExtensionApiHandler implements Action<Chain> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExtensionApiHandler.class);

    private final HandlerHelper handlerHelper;
    private final ArticleBodyFetcher articleBodyFetcher;
    private final ArticleStore articleStore;

    @Inject
    public ExtensionApiHandler(HandlerHelper handlerHelper, ArticleBodyFetcher articleBodyFetcher, ArticleStore store) {
        this.handlerHelper = handlerHelper;
        this.articleBodyFetcher = articleBodyFetcher;
        this.articleStore = store;
    }

    @Override
    public void execute(Chain chain) throws Exception {
        chain.prefix("sof", chain.getRegistry().get(SOFApiHandler.class));
        chain.handler("page", this::handleSavePageAsync);
        chain.handler("selected", this::handleSaveSelectedTextAsync);
        chain.handler("check", this::handleCheckAlreadySavedAnswerAsync);
        chain.handler("redirect/:id", this::handleRedirectAsync);
        chain.handler("suggestion", this::handleSuggestionAsync);
    }

    /**
     * Saving page submitted by extension. The body section of the page is empty, we need to fetch it again and save it.
     *
     * @param context ratpack context
     * @throws Exception
     */
    public void handleSavePageAsync(Context context) throws Exception {
        context.promise(new Action<Fulfiller<String>>() {
            @Override
            public void execute(Fulfiller<String> fulfiller) throws Exception {
                Article article = handlerHelper.fromBody(context, Article.class);
                FetchResponse fetchResponse = articleBodyFetcher.fetchBody(article.getUrl());
                if (!fetchResponse.isValid()) {
                    handlerHelper.jsonConsumer(fulfiller)
                            .accept(IdResponse.create(null).error("Could not fetch the article body."));
                    return;
                }
                try {
                    article.setBody(fetchResponse.getBody());
                    IdResponse response = articleStore.save(article);
                    handlerHelper.jsonConsumer(fulfiller).accept(response);
                } catch (StoreActionFailedException e) {
                    LOGGER.error("Store failed for page, action:{}, id:{}, article:{}",
                            e.getAction(), e.getId(), article, e);
                    handlerHelper.jsonConsumer(fulfiller)
                            .accept(IdResponse.create(e.getId()).error("Could not save article."));
                }
            }
        }).onError(newErrorAction(context))
                .then(newJsonAction(context));
    }

    public void handleSaveSelectedTextAsync(Context context) throws Exception {
        context.promise(new Action<Fulfiller<String>>() {
            @Override
            public void execute(Fulfiller<String> fulfiller) throws Exception {
                Article article = handlerHelper.fromBody(context, Article.class);
                try {
                    //todo: pre-process body of article, detect type of it (e.g. bash script, source code or normal text)
                    //todo: change url of article, the curl url is the url of the page, but the article url must point to pl site.
                    IdResponse response = articleStore.save(article);
                    handlerHelper.jsonConsumer(fulfiller).accept(response);
                } catch (StoreActionFailedException e) {
                    LOGGER.error("Store failed for selected text, action:{}, id:{}, article:{}",
                            e.getAction(), e.getId(), article, e);
                    handlerHelper.jsonConsumer(fulfiller)
                            .accept(IdResponse.create(e.getId()).error("Could not save article."));
                }
            }
        }).onError(newErrorAction(context))
                .then(newJsonAction(context));
    }

    public void handleCheckAlreadySavedAnswerAsync(Context context) throws Exception {
        context.promise(new Action<Fulfiller<String>>() {
            @Override
            public void execute(Fulfiller<String> fulfiller) throws Exception {
                GetByUrlRequest request = handlerHelper.fromBody(context, GetByUrlRequest.class);
                articleStore.asyncGet(request, handlerHelper.jsonConsumer(fulfiller), fulfiller::error);
            }
        }).onError(newErrorAction(context))
                .then(newJsonAction(context));
    }

    public void handleSuggestionAsync(Context context) throws Exception {
        context.promise(new Action<Fulfiller<String>>() {
            @Override
            public void execute(Fulfiller<String> fulfiller) throws Exception {
                PagedRequest request = handlerHelper.fromBody(context, PagedRequest.class);
                articleStore.asyncSuggestion(request, handlerHelper.jsonConsumer(fulfiller), fulfiller::error);
            }
        }).onError(newErrorAction(context))
                .then(newJsonAction(context));
    }

    public void handleRedirectAsync(Context context) throws Exception {
        context.promise(new Action<Fulfiller<String>>() {
            @Override
            public void execute(Fulfiller<String> fulfiller) throws Exception {
                String id = context.getPathTokens().get("id");
                Optional<Article> optional = articleStore.findById(id);
                if (optional.isPresent()) {
                    fulfiller.success(optional.get().getUrl());
                } else {
                    throw new Exception("Article with id:" + id + " not found");
                }
            }
        }).onError(newErrorAction(context))
                .then(context::redirect);
    }
}
