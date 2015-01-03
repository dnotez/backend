package com.pl.extraction;

import com.pl.stream.StreamHelper;
import org.apache.tika.detect.Detector;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author mamad
 * @since 22/11/14.
 */
public class TikaMimeTypeDetector implements MimeTypeDetector {
    private final Detector detector;

    public TikaMimeTypeDetector(Detector detector) {
        this.detector = detector;
    }

    @Override
    public DetectedMimeType detect(InputStream stream, Metadata metadata) throws IOException {
        MediaType mediaType = detector.detect(StreamHelper.toMarkSupported(stream), metadata);
        return new TikaMimeType(mediaType);
    }
}
