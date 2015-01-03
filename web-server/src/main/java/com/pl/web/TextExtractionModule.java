package com.pl.web;

import com.google.inject.AbstractModule;
import com.pl.duplicate.DuplicateStreamDetector;
import com.pl.duplicate.MD5DuplicateStreamDetector;
import com.pl.extraction.PlainTextExtractor;
import com.pl.extraction.TikaPlainTextExtractor;

/**
 * @author mamad
 * @since 16/12/14.
 */
public class TextExtractionModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(DuplicateStreamDetector.class).to(MD5DuplicateStreamDetector.class);
        bind(PlainTextExtractor.class).to(TikaPlainTextExtractor.class);
    }
}
