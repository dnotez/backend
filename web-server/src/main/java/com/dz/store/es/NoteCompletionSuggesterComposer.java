package com.dz.store.es;

import com.dz.dsl.note.Note;
import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Default composer, use title as input field and split it to tokens to be able to suggest on any term
 *
 * @author mamad
 * @since 30/11/14.
 */
public class NoteCompletionSuggesterComposer implements NoteSuggestionComposer {

    public static final Splitter SPLITTER = Splitter
            .on(CharMatcher.WHITESPACE)
            .trimResults()
            .omitEmptyStrings();

    @Override
    public Map<String, Object> createSuggestionObject(Note note) {
        checkNotNull(note);
        String id = checkNotNull(note.getId(), "Note id can not be null.");
        String title = checkNotNull(note.getTitle(), "Note title can not be null.");
        return createSuggestionObject(id, title);
    }

    public Map<String, Object> createSuggestionObject(String id, String title) {
        return ImmutableMap.of(
                "input", Lists.newArrayList(SPLITTER.split(title).iterator()),
                "output", title,
                "payload", ImmutableMap.of(NoteCompletionFields.ID, id));
    }
}
