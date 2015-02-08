package com.dz.body;

import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.io.CharStreams;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.junit.Assert.*;

/**
 * @author mamad
 * @since 07/02/15.
 */
public class BoilerpipeHtmlMainContentExtractorTest {
    BoilerpipeHtmlMainContentExtractor extractor = new BoilerpipeHtmlMainContentExtractor();

    @Test
    public void testExtractWikipediaArticle() throws Exception {
        String html = readHtmlResource("/page1.html");
        String extracted = extract(html, true);
        assertTrue(extracted.contains("(born 1 March 1927)"));
        assertFalse(extracted.contains("References"));
        assertFalse(extracted.contains("External links"));
    }

    private String readHtmlResource(String path) {
        try {
            try (InputStream stream = getClass().getResourceAsStream(path)) {
                return CharStreams.toString(new InputStreamReader(stream, Charsets.UTF_8));
            }
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
        return null;
    }

    @Test
    public void testEmptyContent() {
        extract(null, false);
        extract("", false);
        extract("<html></html>", false);
        extract("<html><body></html>", false);
        extract("<html><body></body></html>", false);
    }

    private String extract(String html, boolean hasMainContent) {
        try {
            Optional<String> optional = extractor.extractMainContent(html);
            assertNotNull(optional);
            if (hasMainContent) {
                assertTrue(optional.isPresent());
                return optional.get();
            }
        } catch (HtmlContentExtractionException e) {
            Assert.fail(e.getMessage());
        }
        return null;
    }
}
