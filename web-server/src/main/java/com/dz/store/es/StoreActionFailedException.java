package com.dz.store.es;

import com.dz.dsl.PagedRequest;

/**
 * @author mamad
 * @since 13/11/14.
 */
public class StoreActionFailedException extends Exception {
    private final Action action;
    private final String id;
    private final boolean timeout;

    public StoreActionFailedException(Action action, String message, Throwable e) {
        super(message, e);
        this.action = action;
        this.id = null;
        this.timeout = false;
    }

    public StoreActionFailedException(Action action, String id) {
        this(action, id, false);
    }

    public StoreActionFailedException(Action action, String id, boolean timeout) {
        this.action = action;
        this.id = id;
        this.timeout = timeout;
    }

    public static StoreActionFailedException createFailed(String id) {
        return new StoreActionFailedException(Action.CREATE, id);
    }

    public static StoreActionFailedException updateFailed(String id) {
        return new StoreActionFailedException(Action.UPDATE, id);
    }

    public static StoreActionFailedException getFailed(String id) {
        return new StoreActionFailedException(Action.GET, id);
    }

    public static <R> StoreActionFailedException searchFailed(PagedRequest request) {
        return new StoreActionFailedException(Action.SEARCH, request.getQuery());
    }

    public static <R> StoreActionFailedException searchTimeout(PagedRequest request) {
        return new StoreActionFailedException(Action.SEARCH, request.getQuery(), true);
    }

    public Action getAction() {
        return action;
    }

    public String getId() {
        return id;
    }

    public boolean isTimeout() {
        return timeout;
    }

    public static enum Action {
        CREATE,
        GET,
        SEARCH,
        UPDATE,
    }

}
