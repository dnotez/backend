package com.dz.store.es.manage;

/**
 * @author mamad
 * @since 14/11/14.
 */
public interface IndexCreator {
    boolean createMappings();

    boolean createIndices();
}
