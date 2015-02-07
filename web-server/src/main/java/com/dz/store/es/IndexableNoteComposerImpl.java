package com.dz.store.es;

import com.dz.dsl.note.Note;
import com.dz.extraction.PlainTextExtraction;
import com.dz.extraction.PlainTextExtractor;
import com.dz.extraction.TextExtractionException;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.ByteSource;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.elasticsearch.common.xcontent.XContentBuilder;

import java.io.IOException;
import java.io.InputStream;

import static com.dz.dsl.note.NoteFields.*;
import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * @author mamad
 * @since 13/11/14.
 */
@Singleton
public class IndexableNoteComposerImpl implements IndexableNoteComposer {
    private final PlainTextExtractor plainTextExtractor;
    private final NoteSuggestionComposer completionSuggestionComposer;

    @Inject
    public IndexableNoteComposerImpl(PlainTextExtractor plainTextExtractor, NoteSuggestionComposer completionSuggestionComposer) {
        this.plainTextExtractor = plainTextExtractor;
        this.completionSuggestionComposer = completionSuggestionComposer;
    }

    @Override
    public XContentBuilder compose(Note note) throws IOException, StoreActionFailedException {
        //todo: move the text extraction to another class
        InputStream stream = ByteSource.wrap(note.getBody().getBytes(Charsets.UTF_8)).openStream();
        try {
            PlainTextExtraction textExtraction = plainTextExtractor.extract(stream, ImmutableMap.of("Content-Type", "text/html"));
            note.setText(textExtraction.getPlainText());
            note.setMimeType(textExtraction.getMimeType().asString());
        } catch (TextExtractionException e) {
            throw new StoreActionFailedException(StoreActionFailedException.Action.CREATE, "Error in extracting text", e);
        }

        return jsonBuilder()
                .startObject()
                .field(TITLE, note.getTitle())
                .field(BODY, note.getBody())
                .field(TEXT, note.getText())
                .field(MD5, note.getMd5())
                .field(MIME_TYPE, note.getMimeType())
                .field(SAVE_DATE, note.getSaveDate())
                .field(LABEL, note.getLabel())
                .field(URL, note.getUrl())
                        //for completion suggestion
                .field(SUGGEST, completionSuggestionComposer.createSuggestionObject(note))
                .field(SUGGEST_PHRASE, note.getTitle())
                .endObject();
    }
}
