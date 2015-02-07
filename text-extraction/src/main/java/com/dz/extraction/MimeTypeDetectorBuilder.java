package com.dz.extraction;

import org.apache.tika.Tika;

/**
 * @author mamad
 * @since 22/11/14.
 */
public class MimeTypeDetectorBuilder {
    public static MimeTypeDetectorBuilder create() {
        return new MimeTypeDetectorBuilder();
    }

    public MimeTypeDetector defaultDetector() {
        return new TikaMimeTypeDetector(new Tika().getDetector());
    }
}
