package com.dz.web.api.article;

import com.dz.dsl.PagedRequest;
import com.dz.dsl.PagedResponse;
import com.dz.store.es.NoteStore;
import com.dz.web.AsyncHelper;
import com.dz.web.HandlerHelper;
import com.dz.web.QueryParams;
import com.dz.web.StatusHelper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import ratpack.exec.Fulfiller;
import ratpack.func.Action;
import ratpack.handling.Chain;
import ratpack.handling.Context;

/**
 * @author mamad
 * @since 16/12/14.
 * @deprecated Replaced with com.dz.web.user.note.NotesChainHandler
 */
@Deprecated
@Singleton
public class ArticleCollectionHandler implements Action<Chain> {
    private final HandlerHelper handlerHelper;

    private final NoteStore noteStore;

    @Inject
    public ArticleCollectionHandler(HandlerHelper handlerHelper, NoteStore noteStore) {
        this.handlerHelper = handlerHelper;
        this.noteStore = noteStore;
    }

    @Override
    public void execute(Chain chain) throws Exception {
        chain.get(this::asyncCollectionHandler);
        chain.post(this::asyncCollectionPostHandler);
    }

    public void asyncCollectionPostHandler(Context context) throws Exception {
        PagedRequest request = handlerHelper.fromBody(context, PagedRequest.class);
        handleRequestAsync(request, context);
    }

    public void asyncCollectionHandler(Context context) throws Exception {
        PagedRequest request = new PagedRequest();

        QueryParams params = QueryParams.create(context.getRequest().getQueryParams());
        request.setStartIndex(params.valueOf("startIndex", 0));
        request.setPageSize(params.valueOf("pageSize", 10));
        request.setQuery(params.valueOf("query", null));
        handleRequestAsync(request, context);
    }

    protected void handleRequestAsync(PagedRequest request, Context context) throws Exception {
        context.promise(new Action<Fulfiller<String>>() {
            @Override
            public void execute(Fulfiller<String> fulfiller) throws Exception {
                noteStore.asyncList(request, handlerHelper.jsonConsumer(fulfiller, PagedResponse::getResults),
                        fulfiller::error);
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
