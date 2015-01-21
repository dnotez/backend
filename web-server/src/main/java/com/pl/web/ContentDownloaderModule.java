package com.pl.web;

import com.google.inject.AbstractModule;
import com.pl.fetch.NoteBodyFetcher;
import com.pl.fetch.NoteBodyFetcherImpl;

/**
 * @author mamad
 * @since 16/12/14.
 */
public class ContentDownloaderModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(NoteBodyFetcher.class).to(NoteBodyFetcherImpl.class);
    }
}
