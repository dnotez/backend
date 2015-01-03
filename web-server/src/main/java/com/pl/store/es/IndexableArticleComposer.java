package com.pl.store.es;

import com.pl.dsl.article.Article;
import org.elasticsearch.common.xcontent.XContentBuilder;

import java.io.IOException;

/**
 * Fill all required fields before indexing an article.
 *
 * @author mamad
 * @since 30/11/14.
 */
public interface IndexableArticleComposer {
    XContentBuilder compose(Article article) throws IOException, StoreActionFailedException;
}
