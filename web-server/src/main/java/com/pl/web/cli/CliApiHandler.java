package com.pl.web.cli;

import com.google.inject.Inject;
import com.pl.bean.ArgChecker;
import com.pl.dsl.GetByKeyRequest;
import com.pl.dsl.GetByKeyRequest.KeyType;
import com.pl.dsl.PagedRequest;
import com.pl.dsl.cli.CliSearchRequest;
import com.pl.dsl.cli.SaveCmdRequest;
import com.pl.store.es.NoteStore;
import com.pl.string.ParseUtils;
import com.pl.web.HandlerHelper;
import ratpack.exec.Fulfiller;
import ratpack.func.Action;
import ratpack.handling.Chain;
import ratpack.handling.Context;
import ratpack.util.MultiValueMap;

import static com.pl.dsl.GetByKeyRequest.KeyType.STRING_VALUES;
import static com.pl.string.ParseUtils.safeValue;
import static com.pl.web.AsyncHelper.newErrorAction;
import static com.pl.web.AsyncHelper.newJsonAction;
import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_OK;

/**
 * @author mamad
 * @since 11/12/14.
 */
public class CliApiHandler implements Action<Chain> {
    private final HandlerHelper handlerHelper;
    private final NoteStore noteStore;
    private final SaveCmdPersister cmdPersister;

    @Inject
    public CliApiHandler(HandlerHelper handlerHelper, NoteStore store, SaveCmdPersister cmdPersister) {
        this.handlerHelper = handlerHelper;
        this.noteStore = store;
        this.cmdPersister = cmdPersister;
    }

    @Override
    public void execute(Chain chain) throws Exception {
        chain.post("cmd", this::handleSaveCmdRequestAsync);
        //both delete and get needs a param
        chain.prefix("cmd/:param", action -> action.handler(ctx -> {
            ctx.byMethod(spec -> spec.delete(this::handleDeleteAsync).get(this::handleGetByKeyAsync));
        }));
        chain.handler("search", this::handleCliSearchRequestAsync);
    }

    void handleGetByKeyAsync(Context context) throws Exception {
        context.promise(new Action<Fulfiller<String>>() {
            @Override
            public void execute(Fulfiller<String> fulfiller) throws Exception {
                String key = context.getPathTokens().get("param");
                MultiValueMap<String, String> queryParams = context.getRequest().getQueryParams();
                String keyType = queryParams.get("k");
                String n = queryParams.get("n");
                ArgChecker.create()
                        .notEmpty(key, "Key can not be null.")
                                //.nullOr(keyType, "Key type must be any of", KeyType.STRING_VALUES, true)
                        .add(keyTypeValue -> keyTypeValue == null || STRING_VALUES.contains(keyTypeValue.toUpperCase()),
                                keyType, "Key type must be any of id, label")
                        .nullOrNumber(n, "Invalid number argument.")
                        .verify();
                int count = ParseUtils.minAcceptableValue(n, 1);
                GetByKeyRequest request = new GetByKeyRequest(key, count, safeValue(keyType, KeyType.ID));
                noteStore.asyncGet(request,
                        handlerHelper.jsonConsumer(fulfiller),
                        fulfiller::error);
            }
        }).onError(newErrorAction(context))
                .then(newJsonAction(context, HTTP_OK));
    }

    void handleDeleteAsync(Context context) throws Exception {
        context.promise(new Action<Fulfiller<String>>() {
            @Override
            public void execute(Fulfiller<String> fulfiller) throws Exception {
                String id = context.getPathTokens().get("param");
                ArgChecker.create().notNull(id, "Id can not be null.").verify();
                noteStore.asyncDelete(id, handlerHelper.jsonConsumer(fulfiller), fulfiller::error);
            }
        }).onError(newErrorAction(context))
                .then(newJsonAction(context, HTTP_OK));
    }

    void handleSaveCmdRequestAsync(Context context) throws Exception {
        context.promise(new Action<Fulfiller<String>>() {
            @Override
            public void execute(Fulfiller<String> fulfiller) throws Exception {
                SaveCmdRequest request = handlerHelper.fromBody(context, SaveCmdRequest.class);
                ArgChecker.create()
                        .notEmpty(request.getBody(), "Command body can not be null.")
                        .verify();
                cmdPersister.persist(request, handlerHelper.jsonConsumer(fulfiller), fulfiller::error);
            }
        }).onError(newErrorAction(context))
                .then(newJsonAction(context, HTTP_CREATED));
    }

    void handleCliSearchRequestAsync(Context context) throws Exception {
        context.promise(new Action<Fulfiller<String>>() {
            @Override
            public void execute(Fulfiller<String> fulfiller) throws Exception {
                CliSearchRequest request = handlerHelper.fromBody(context, CliSearchRequest.class);
                PagedRequest pagedRequest = new PagedRequest(request.getLabel());
                noteStore.asyncList(pagedRequest, handlerHelper.jsonConsumer(fulfiller), fulfiller::error);
            }
        }).onError(newErrorAction(context))
                .then(newJsonAction(context, HTTP_OK));
    }
}
