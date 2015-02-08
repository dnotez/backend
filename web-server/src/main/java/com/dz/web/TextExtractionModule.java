package com.dz.web;

import com.dz.body.BoilerpipeHtmlMainContentExtractor;
import com.dz.body.HtmlMainContentExtractor;
import com.dz.duplicate.DuplicateStreamDetector;
import com.dz.duplicate.MD5DuplicateStreamDetector;
import com.dz.extraction.PlainTextExtractor;
import com.dz.extraction.TikaPlainTextExtractor;
import com.google.inject.AbstractModule;

/**
 * @author mamad
 * @since 16/12/14.
 */
public class TextExtractionModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(DuplicateStreamDetector.class).to(MD5DuplicateStreamDetector.class);
        bind(PlainTextExtractor.class).to(TikaPlainTextExtractor.class);
        bind(HtmlMainContentExtractor.class).to(BoilerpipeHtmlMainContentExtractor.class);
    }
}
