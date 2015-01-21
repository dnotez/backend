package com.pl.store.es;

import com.pl.dsl.note.Note;

import java.util.Map;

/**
 * This composer is used to compose completion field NoteFields.SUGGEST.
 * Use the title as input and id of the note as the payload
 *
 * @author mamad
 * @since 30/11/14.
 */
public interface NoteSuggestionComposer {
    Map<String, Object> createSuggestionObject(Note note);
}
