package com.dz.web.extension;

import com.dz.dsl.IdResponse;
import com.dz.dsl.note.Note;
import com.dz.store.es.NoteStore;
import com.dz.store.es.StoreActionFailedException;
import com.dz.web.HandlerHelper;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.exec.Fulfiller;
import ratpack.func.Action;
import ratpack.handling.Chain;
import ratpack.handling.Context;

import java.net.HttpURLConnection;

import static com.dz.web.AsyncHelper.newErrorAction;
import static com.dz.web.AsyncHelper.newJsonAction;

/**
 * Handler to save stackoverflow.com answer
 *
 * @author mamad
 * @since 23/11/14.
 */
public class SOFApiHandler implements Action<Chain> {
    public static final Logger LOGGER = LoggerFactory.getLogger(SOFApiHandler.class);

    private final HandlerHelper handlerHelper;
    private final NoteStore noteStore;

    @Inject
    public SOFApiHandler(HandlerHelper handlerHelper, NoteStore store) {
        this.handlerHelper = handlerHelper;
        this.noteStore = store;
    }

    @Override
    public void execute(Chain chain) throws Exception {
        chain.handler("answer", this::handleSaveAnswerAsync);
    }


    public void handleSaveAnswerAsync(Context context) throws Exception {
        context.promise(new Action<Fulfiller<String>>() {
            @Override
            public void execute(Fulfiller<String> fulfiller) throws Exception {
                Note note = handlerHelper.fromBody(context, Note.class);
                try {
                    IdResponse response = noteStore.save(note);
                    handlerHelper.jsonConsumer(fulfiller).accept(response);
                } catch (StoreActionFailedException e) {
                    LOGGER.error("Store failed for SOF:{}, action:{}, id:{}, note:{}", note.getUrl(), e.getAction(), e.getId(), note, e);
                    handlerHelper.jsonConsumer(fulfiller)
                            .accept(IdResponse.create(e.getId()).error("Could not save note"));
                }
            }
        }).onError(newErrorAction(context))
                .then(newJsonAction(context, HttpURLConnection.HTTP_CREATED));
    }
}
