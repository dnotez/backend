package com.dz.extraction;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * @author mamad
 * @since 22/11/14.
 */
public interface PlainTextExtractor {
    PlainTextExtraction extract(InputStream stream, Map<String, Object> metadata) throws IOException, TextExtractionException;
}
