package com.dz.extraction;

import org.apache.tika.metadata.Metadata;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author mamad
 * @since 22/11/14.
 */
public interface MimeTypeDetector {

    DetectedMimeType detect(InputStream stream, Metadata metadata) throws IOException;
}
