package com.pl.web;

import com.google.inject.AbstractModule;
import com.pl.fetch.ArticleBodyFetcher;
import com.pl.fetch.ArticleBodyFetcherImpl;

/**
 * @author mamad
 * @since 16/12/14.
 */
public class ContentDownloaderModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ArticleBodyFetcher.class).to(ArticleBodyFetcherImpl.class);
    }
}
