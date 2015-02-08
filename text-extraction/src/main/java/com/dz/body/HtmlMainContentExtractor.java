package com.dz.body;

import com.google.common.base.Optional;

/**
 * Extract body (main content) form the html input.
 *
 * @author mamad
 * @since 07/02/15.
 */
public interface HtmlMainContentExtractor {
    Optional<String> extractMainContent(String html) throws HtmlContentExtractionException;
}
