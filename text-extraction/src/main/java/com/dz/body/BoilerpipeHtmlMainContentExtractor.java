package com.dz.body;

import com.google.common.base.Optional;
import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.document.TextDocument;
import de.l3s.boilerpipe.extractors.CommonExtractors;
import de.l3s.boilerpipe.sax.BoilerpipeSAXInput;
import de.l3s.boilerpipe.sax.HTMLHighlighter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.StringReader;

import static com.google.common.base.Strings.nullToEmpty;

/**
 * Extract main html content using Boilerpipe ARTICLE_EXTRACTOR and HTMLHighlighter.
 * No HTML or BODY tag will be included in the response.
 *
 * @author mamad
 * @since 07/02/15.
 */
public class BoilerpipeHtmlMainContentExtractor implements HtmlMainContentExtractor {
    private static final Logger LOGGER = LoggerFactory.getLogger(BoilerpipeHtmlMainContentExtractor.class);
    private static final HTMLHighlighter HTML_HIGHLIGHTER = HTMLHighlighter.newExtractingInstance();

    @Override
    public Optional<String> extractMainContent(String originalHtml) throws HtmlContentExtractionException {
        TextDocument doc;
        String html = nullToEmpty(originalHtml);
        try {
            doc = new BoilerpipeSAXInput(new InputSource(
                    new StringReader(html))).getTextDocument();
        } catch (BoilerpipeProcessingException e) {
            LOGGER.error("Boilerpipe error when creating doc", e);
            throw new HtmlContentExtractionException("Could not create text document from the input html", e);
        } catch (SAXException e) {
            throw new HtmlContentExtractionException("SAX parsing error", e);
        }

        try {
            CommonExtractors.ARTICLE_EXTRACTOR.process(doc);
        } catch (BoilerpipeProcessingException e) {
            LOGGER.error("Boilerpipe error when extracting main content", e);
            throw new HtmlContentExtractionException("Could not extract main content from the input html", e);
        }

        try {
            String highlight = HTML_HIGHLIGHTER.process(doc, html);
            String lowerCased = highlight.toLowerCase();
            int bodyStart = lowerCased.indexOf("<body");
            if (bodyStart < 1) {
                //no body tag in the response
                return Optional.absent();
            }
            int bodyOpen = lowerCased.substring(bodyStart).indexOf('>');
            int bodyClose = lowerCased.indexOf("</body>");
            String mainContent = highlight.substring(bodyStart + bodyOpen + 1, bodyClose);
            return Optional.of(mainContent);
        } catch (BoilerpipeProcessingException e) {
            LOGGER.error("Boilerpipe error when extracting html content", e);
            throw new HtmlContentExtractionException("Could not extract html content from the input html", e);
        }
    }
}
