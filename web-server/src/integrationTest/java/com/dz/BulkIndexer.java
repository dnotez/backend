package com.dz;


import com.dz.store.es.NoteStore;

/**
 * @author mamad
 * @since 29/11/14.
 */
public interface BulkIndexer {
    int index(NoteStore store);
}
