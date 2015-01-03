package com.pl.store.es;

/**
 * @author mamad
 * @since 13/11/14.
 */
public class DefaultTimeouts implements ActionTimeouts {

    public static final int DEFAULT_TIMEOUT = 10000;

    private final int timeout;

    public DefaultTimeouts() {
        this(DEFAULT_TIMEOUT);
    }

    public DefaultTimeouts(int timeout) {
        this.timeout = timeout;
    }

    @Override
    public long indexTimeoutMillis() {
        return timeout;
    }

    @Override
    public long searchTimeoutMillis() {
        return timeout;
    }
}
