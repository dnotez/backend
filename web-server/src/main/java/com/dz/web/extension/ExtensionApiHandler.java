package com.dz.web.extension;

import com.dz.body.HtmlMainContentExtractor;
import com.dz.dsl.IdResponse;
import com.dz.dsl.PagedRequest;
import com.dz.dsl.extension.GetByUrlRequest;
import com.dz.dsl.note.Note;
import com.dz.fetch.FetchResponse;
import com.dz.fetch.NoteBodyFetcher;
import com.dz.store.es.NoteStore;
import com.dz.store.es.StoreActionFailedException;
import com.dz.web.HandlerHelper;
import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.exec.Fulfiller;
import ratpack.func.Action;
import ratpack.handling.Chain;
import ratpack.handling.Context;

import static com.dz.web.AsyncHelper.newErrorAction;
import static com.dz.web.AsyncHelper.newJsonAction;

/**
 * @author mamad
 * @since 23/11/14.
 */
@Singleton
public class ExtensionApiHandler implements Action<Chain> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExtensionApiHandler.class);

    private final HandlerHelper handlerHelper;
    private final NoteBodyFetcher noteBodyFetcher;
    private final HtmlMainContentExtractor mainContentExtractor;
    private final NoteStore noteStore;

    @Inject
    public ExtensionApiHandler(HandlerHelper handlerHelper, NoteBodyFetcher noteBodyFetcher,
                               HtmlMainContentExtractor mainContentExtractor, NoteStore store) {
        this.handlerHelper = handlerHelper;
        this.noteBodyFetcher = noteBodyFetcher;
        this.mainContentExtractor = mainContentExtractor;
        this.noteStore = store;
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
                Note note = handlerHelper.fromBody(context, Note.class);
                //fetch full content
                FetchResponse fetchResponse = noteBodyFetcher.fetchBody(note.getUrl());
                if (!fetchResponse.isValid()) {
                    handlerHelper.jsonConsumer(fulfiller)
                            .accept(IdResponse.create(null).error("Could not fetch the note body."));
                    return;
                }

                //extract main content
                Optional<String> optional = mainContentExtractor.extractMainContent(fetchResponse.getBody());
                if (!optional.isPresent()) {
                    handlerHelper.jsonConsumer(fulfiller)
                            .accept(IdResponse.create(null).error("Page has no content."));
                    return;
                }

                try {
                    note.setBody(optional.get());
                    IdResponse response = noteStore.save(note);
                    handlerHelper.jsonConsumer(fulfiller).accept(response);
                } catch (StoreActionFailedException e) {
                    LOGGER.error("Store failed for page, action:{}, id:{}, note:{}",
                            e.getAction(), e.getId(), note, e);
                    handlerHelper.jsonConsumer(fulfiller)
                            .accept(IdResponse.create(e.getId()).error("Could not save note."));
                }
            }
        }).onError(newErrorAction(context))
                .then(newJsonAction(context));
    }

    public void handleSaveSelectedTextAsync(Context context) throws Exception {
        context.promise(new Action<Fulfiller<String>>() {
            @Override
            public void execute(Fulfiller<String> fulfiller) throws Exception {
                Note note = handlerHelper.fromBody(context, Note.class);
                try {
                    //todo: pre-process body of note, detect type of it (e.g. bash script, source code or normal text)
                    //todo: change url of note, the curl url is the url of the page, but the note url must point to dz site.
                    IdResponse response = noteStore.save(note);
                    handlerHelper.jsonConsumer(fulfiller).accept(response);
                } catch (StoreActionFailedException e) {
                    LOGGER.error("Store failed for selected text, action:{}, id:{}, note:{}",
                            e.getAction(), e.getId(), note, e);
                    handlerHelper.jsonConsumer(fulfiller)
                            .accept(IdResponse.create(e.getId()).error("Could not save note."));
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
                noteStore.asyncGet(request, handlerHelper.jsonConsumer(fulfiller), fulfiller::error);
            }
        }).onError(newErrorAction(context))
                .then(newJsonAction(context));
    }

    public void handleSuggestionAsync(Context context) throws Exception {
        context.promise(new Action<Fulfiller<String>>() {
            @Override
            public void execute(Fulfiller<String> fulfiller) throws Exception {
                PagedRequest request = handlerHelper.fromBody(context, PagedRequest.class);
                noteStore.asyncSuggestion(request, handlerHelper.jsonConsumer(fulfiller), fulfiller::error);
            }
        }).onError(newErrorAction(context))
                .then(newJsonAction(context));
    }

    public void handleRedirectAsync(Context context) throws Exception {
        context.promise(new Action<Fulfiller<String>>() {
            @Override
            public void execute(Fulfiller<String> fulfiller) throws Exception {
                String id = context.getPathTokens().get("id");
                Optional<Note> optional = noteStore.findById(id);
                if (optional.isPresent()) {
                    fulfiller.success(optional.get().getUrl());
                } else {
                    throw new Exception("Note with id:" + id + " not found");
                }
            }
        }).onError(newErrorAction(context))
                .then(context::redirect);
    }
}
