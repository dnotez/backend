package com.dz.extraction;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static com.dz.stream.StreamHelper.toMarkSupported;

/**
 * @author mamad
 * @since 22/11/14.
 */
public class TikaPlainTextExtractor implements PlainTextExtractor {
    private final Parser parser;
    private final MimeTypeDetector mimeTypeDetector;

    public TikaPlainTextExtractor() {
        this(ParserBuilder.create().defaultAutoDetectParser(), MimeTypeDetectorBuilder.create().defaultDetector());
    }

    public TikaPlainTextExtractor(Parser parser, MimeTypeDetector mimeTypeDetector) {
        this.parser = parser;
        this.mimeTypeDetector = mimeTypeDetector;
    }


    @Override
    public PlainTextExtraction extract(InputStream origStream, Map<String, Object> metadata) throws IOException, TextExtractionException {
        InputStream stream = toMarkSupported(origStream);
        ContentHandler textContentHandler = new BodyContentHandler();
        Metadata tikaMetadata = new Metadata();
        for (Map.Entry<String, Object> entry : metadata.entrySet()) {
            tikaMetadata.add(entry.getKey(), entry.getValue().toString());
        }
        ParseContext context = new ParseContext();
        try {
            parser.parse(stream, textContentHandler, tikaMetadata, context);
            DetectedMimeType mimeType = mimeTypeDetector.detect(stream, tikaMetadata);
            return new PlainTextExtraction(mimeType, textContentHandler.toString().trim());
        } catch (SAXException e) {
            throw new TextExtractionException("Parser error whiling parsing stream.", e);
        } catch (TikaException e) {
            throw new TextExtractionException("Could not extract text from stream.", e);
        }
    }
}
