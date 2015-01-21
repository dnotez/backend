package com.pl.web.cli;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.pl.dsl.note.Note;
import com.pl.dsl.note.Type;
import com.pl.dsl.cli.SaveCmdRequest;
import com.pl.dsl.cli.SaveCmdResponse;
import com.pl.store.es.NoteStore;
import com.pl.store.es.UUIDGenerator;

import java.util.function.Consumer;

/**
 * @author mamad
 * @since 13/12/14.
 */
@Singleton
public class SaveCmdPersisterImpl implements SaveCmdPersister {
    private final NoteStore noteStore;
    private final SaveCmdTitleBuilder titleBuilder;
    private final SaveCmdHtmlBodyBuilder bodyBuilder;
    private final SaveCmdUrlBuilder urlBuilder;
    private final UUIDGenerator uuidGenerator;

    @Inject
    public SaveCmdPersisterImpl(NoteStore noteStore, SaveCmdTitleBuilder titleBuilder,
                                SaveCmdHtmlBodyBuilder bodyBuilder, SaveCmdUrlBuilder urlBuilder,
                                UUIDGenerator uuidGenerator) {
        this.noteStore = noteStore;
        this.titleBuilder = titleBuilder;
        this.bodyBuilder = bodyBuilder;
        this.urlBuilder = urlBuilder;
        this.uuidGenerator = uuidGenerator;
    }

    @Override
    public void persist(SaveCmdRequest request, Consumer<SaveCmdResponse> onSuccess, Consumer<Throwable> onError) {
        //convert request to note
        Note note = new Note();
        String id = uuidGenerator.newId();
        note.setId(id);
        note.setBody(bodyBuilder.bodyOf(request));
        note.setMimeType("text/plain");
        note.setType(Type.BASH_CMD);
        note.setTitle(titleBuilder.titleOf(request));
        String url = urlBuilder.urlOf(request, id);
        note.setUrl(url);
        note.setLabel(request.getLabel());
        noteStore.asyncSave(note, response -> onSuccess.accept(new SaveCmdResponse(url, id)), onError);
    }
}
