package com.pl.store.es;

/**
 * @author mamad
 * @since 14/12/14.
 */
public class ArticleNotFoundException extends Throwable {
    private final String id;

    public ArticleNotFoundException(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
