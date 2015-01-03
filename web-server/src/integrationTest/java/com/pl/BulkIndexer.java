package com.pl;


import com.pl.store.es.ArticleStore;

/**
 * @author mamad
 * @since 29/11/14.
 */
public interface BulkIndexer {
    int index(ArticleStore store);
}
