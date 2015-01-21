package com.pl.store.es;

import com.pl.dsl.note.Note;

/**
 * @author mamad
 * @since 13/12/14.
 */
public class DuplicateNoteException extends Throwable {
    private final Note note;

    public DuplicateNoteException(Note note) {
        this.note = note;
    }

    public Note getNote() {
        return note;
    }
}
