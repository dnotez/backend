package com.dz.web.user.note;

import com.dz.dsl.IdResponse;
import com.dz.dsl.PagedRequest;
import com.dz.dsl.ResourceResponse;
import com.dz.dsl.note.Note;
import com.dz.store.es.NoteStore;
import com.dz.store.es.StoreActionFailedException;
import com.dz.web.AsyncHelper;
import com.dz.web.HandlerHelper;
import com.dz.web.QueryParams;
import com.dz.web.StatusHelper;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.exec.Fulfiller;
import ratpack.func.Action;
import ratpack.handling.Chain;
import ratpack.handling.Context;

import java.io.IOException;
import java.net.HttpURLConnection;

import static com.dz.web.AsyncHelper.newErrorAction;
import static com.dz.web.AsyncHelper.newJsonAction;

/**
 * mapped to /users/{user}/notes
 *
 * @author mamad
 * @since 23/01/15.
 */
@Singleton
public class NotesChainHandler implements Action<Chain> {
    private static final Logger LOGGER = LoggerFactory.getLogger(NotesChainHandler.class);
    private final HandlerHelper handlerHelper;
    private final NoteStore noteStore;

    @Inject
    public NotesChainHandler(HandlerHelper handlerHelper, NoteStore noteStore) {
        this.handlerHelper = handlerHelper;
        this.noteStore = noteStore;
    }

    @Override
    public void execute(Chain chain) throws Exception {
        //search controller /users/{user}/notes/search
        chain.post("search", this::handleSearchAsync)
                // /users/{user}/notes/{id}
                .handler(":id", ctx -> ctx.byMethod(spec -> spec
                        .get(this::handleGetNoteAsync)
                        .put(this::handleUpdateNoteAsync)
                        .delete(this::handleDeleteNoteAsync)))
                        //read only collection  /users/{user}/notes
                .handler(ctx -> ctx.byMethod(spec -> spec
                        .get(this::handleListAsync)
                        .post(this::handleCreateNoteAsync)));
    }

    public void handleDeleteNoteAsync(Context context) throws Exception {
        context.promise(new Action<Fulfiller<String>>() {
            @Override
            public void execute(Fulfiller<String> fulfiller) throws Exception {
                Optional<String> optional = getIdParameter(context);
                if (optional.isPresent()) {
                    IdResponse response = noteStore.delete(optional.get());
                    handlerHelper.jsonConsumer(fulfiller).accept(response);
                }
            }
        })
                .onError(newErrorAction(context))
                .then(newJsonAction(context));

    }

    private Optional<String> getIdParameter(Context context) {
        String id = context.getPathTokens().get("id");
        if (Strings.isNullOrEmpty(id)) {
            StatusHelper.sendBadRequest(context, "Bad id");
            return Optional.absent();
        } else {
            return Optional.of(id);
        }
    }

    public void handleUpdateNoteAsync(Context context) throws Exception {
        context.promise(new Action<Fulfiller<String>>() {
            @Override
            public void execute(Fulfiller<String> fulfiller) throws Exception {
                Optional<Note> optional = parseRequestBody(context);
                if (!optional.isPresent()) {
                    return;
                }
                noteStore.asyncUpdate(optional.get(), handlerHelper.jsonConsumer(fulfiller), fulfiller::error);
            }
        })
                .onError(newErrorAction(context))
                .then(newJsonAction(context));
    }

    public void handleGetNoteAsync(Context context) throws Exception {
        context.promise(new Action<Fulfiller<String>>() {
            @Override
            public void execute(Fulfiller<String> fulfiller) throws Exception {
                Optional<String> idOptional = getIdParameter(context);
                if (!idOptional.isPresent()) {
                    return;
                }
                Optional<Note> optional;
                try {
                    optional = noteStore.findById(idOptional.get());
                    if (optional.isPresent()) {
                        handlerHelper.jsonConsumer(fulfiller).accept(new ResourceResponse<>(optional.get()));
                    } else {
                        StatusHelper.sendNotFound(context, idOptional.get());
                    }
                } catch (IOException e) {
                    LOGGER.error("IO error during getting note with id:{}", idOptional.get(), e);
                    StatusHelper.sendInternalError(context, e);
                } catch (StoreActionFailedException e) {
                    LOGGER.error("Store failed, could not find note with id:{}", idOptional.get(), e);
                    StatusHelper.sendInternalError(context, e);
                }
            }
        }).onError(newErrorAction(context))
                .then(newJsonAction(context));
    }

    public void handleCreateNoteAsync(Context context) throws Exception {
        //IdResponse
        context.promise(new Action<Fulfiller<String>>() {
            @Override
            public void execute(Fulfiller<String> fulfiller) throws Exception {
                Optional<Note> optional = parseRequestBody(context);
                if (!optional.isPresent()) {
                    return;
                }
                Note note = optional.get();
                noteStore.asyncSave(note, handlerHelper.jsonConsumer(fulfiller), fulfiller::error);
            }
        })
                .onError(newErrorAction(context))
                .then(newJsonAction(context, HttpURLConnection.HTTP_CREATED));
    }

    private Optional<Note> parseRequestBody(Context context) {
        try {
            Note note = handlerHelper.fromBody(context, Note.class);
            return Optional.of(note);
        } catch (Exception e) {
            LOGGER.error("Error in parsing the post body", e);
            StatusHelper.sendBadRequest(context, "Could not parse the post body");
            return Optional.absent();
        }
    }

    public void handleListAsync(Context context) throws Exception {
        PagedRequest request = new PagedRequest();

        QueryParams params = QueryParams.create(context.getRequest().getQueryParams());
        request.setStartIndex(params.valueOf("startIndex", 0));
        request.setPageSize(params.valueOf("pageSize", 10));
        request.setQuery(params.valueOf("query", null));
        handleRequestAsync(request, context);
    }

    public void handleSearchAsync(Context context) throws Exception {
        PagedRequest request;
        try {
            request = handlerHelper.fromBody(context, PagedRequest.class);
        } catch (Exception e) {
            LOGGER.error("Could not parse search request", e);
            StatusHelper.sendBadRequest(context, "Bad request");
            return;
        }
        handleRequestAsync(request, context);
    }

    private void handleRequestAsync(PagedRequest request, Context context) throws Exception {
        context.promise(new Action<Fulfiller<String>>() {
            @Override
            public void execute(Fulfiller<String> fulfiller) throws Exception {
                noteStore.asyncList(request, handlerHelper.jsonConsumer(fulfiller),
                        fulfiller::error);
            }
        })
                .onError(newErrorAction(context))
                .then(AsyncHelper.newJsonAction(context));

    }
}
