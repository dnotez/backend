package com.dz.store.es;

import com.dz.dsl.note.Note;
import org.elasticsearch.common.xcontent.XContentBuilder;

import java.io.IOException;

/**
 * Fill all required fields before indexing an note.
 *
 * @author mamad
 * @since 30/11/14.
 */
public interface IndexableNoteComposer {
    XContentBuilder compose(Note note) throws IOException, StoreActionFailedException;
}
