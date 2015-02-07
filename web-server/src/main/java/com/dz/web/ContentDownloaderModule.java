package com.dz.web;

import com.dz.fetch.NoteBodyFetcher;
import com.dz.fetch.NoteBodyFetcherImpl;
import com.google.inject.AbstractModule;

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
