package com.dz.extraction;

import com.google.common.collect.ImmutableMap;
import org.apache.tika.metadata.Metadata;
import org.junit.Test;

import java.io.ByteArrayInputStream;

import static org.junit.Assert.*;

/**
 * @author mamad
 * @since 22/11/14.
 */
public class TikaPlainTextExtractorIntegTest {
    @Test
    public void testExtractFromHtml() throws Exception {
        TikaPlainTextExtractor textExtractor = new TikaPlainTextExtractor();
        String html = "<p>test</p>";
        ByteArrayInputStream stream = new ByteArrayInputStream(html.getBytes());
        //for html text snippets, we have to tell parse type of the text, otherwise it will be detected as plain text
        PlainTextExtraction extraction = textExtractor.extract(stream, ImmutableMap.of("Content-Type", "text/html"));
        assertNotNull(extraction);
        assertNotNull(extraction.getPlainText());
        assertEquals("test", extraction.getPlainText());
        assertNotNull(extraction.getMimeType());
        assertEquals("text/html", extraction.getMimeType().asString());
        assertTrue(extraction.getMimeType().hasParameters());

    }

    @Test
    public void testMimeTypeDetection() throws Exception {
        String html = "<p>test</p>";
        ByteArrayInputStream stream = new ByteArrayInputStream(html.getBytes());
        DetectedMimeType detectedMimeType = MimeTypeDetectorBuilder.create().defaultDetector().detect(stream, new Metadata());
        assertNotNull(detectedMimeType);
        assertEquals("text/plain", detectedMimeType.asString());
        assertFalse(detectedMimeType.hasParameters());
    }
}
