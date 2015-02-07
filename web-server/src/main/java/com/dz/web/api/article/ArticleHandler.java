package com.dz.web.api.article;

import com.dz.dsl.IdResponse;
import com.dz.dsl.PagedRequest;
import com.dz.dsl.note.Note;
import com.dz.store.es.NoteStore;
import com.dz.store.es.StoreActionFailedException;
import com.dz.web.AsyncHelper;
import com.dz.web.HandlerHelper;
import com.dz.web.StatusHelper;
import com.dz.web.api.DAOHandler;
import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import ratpack.exec.Fulfiller;
import ratpack.func.Action;
import ratpack.handling.Context;

import java.io.IOException;

/**
 * @author mamad
 * @since 13/11/14.
 * @deprecated Replaced with com.dz.web.user.note.NotesChainHandler
 */
@Singleton
@Deprecated
public class ArticleHandler extends DAOHandler<Note> {

    private final NoteStore noteStore;

    @Inject
    public ArticleHandler(NoteStore noteStore, HandlerHelper handlerHelper) {
        super(Note.class, handlerHelper);
        this.noteStore = noteStore;
    }

    @Override
    protected Optional<Note> findById(String id) throws IOException, StoreActionFailedException {
        return noteStore.findById(id);
    }

    @Override
    protected IdResponse save(Note object) throws IOException, StoreActionFailedException {
        return noteStore.save(object);
    }

    @Override
    protected IdResponse update(Note object) {
        return IdResponse.create(object.getId()).error("Not implemented.");
    }

    @Override
    protected IdResponse delete(String id) {
        return noteStore.delete(id);
    }

    @Override
    public void asyncList(PagedRequest request, Context context) {
        context.promise(new Action<Fulfiller<String>>() {
            @Override
            public void execute(Fulfiller<String> fulfiller) throws Exception {
                noteStore.asyncList(request, handlerHelper.jsonConsumer(fulfiller), fulfiller::error);
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
