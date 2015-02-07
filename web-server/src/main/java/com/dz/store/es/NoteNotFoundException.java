package com.dz.store.es;

/**
 * @author mamad
 * @since 14/12/14.
 */
public class NoteNotFoundException extends Throwable {
    private final String id;

    public NoteNotFoundException(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
