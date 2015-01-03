package com.pl.store.es;

/**
 * @author mamad
 * @since 13/11/14.
 */
public interface ActionTimeouts {

    long indexTimeoutMillis();

    long searchTimeoutMillis();
}
