package com.pl.stream;

import java.io.BufferedInputStream;
import java.io.InputStream;

/**
 * Utility class for working with streams
 *
 * @author mamad
 * @since 22/11/14.
 */
public final class StreamHelper {
    /**
     * Convert input stream to buffer stream if mark is not supported. This is required when stream re-read multiple times
     * (e.g. detecting mime-type before parsing).
     *
     * @param stream input stream
     * @return mark supported stream
     */
    public static InputStream toMarkSupported(InputStream stream) {
        if (stream.markSupported()) {
            return stream;
        } else {
            return new BufferedInputStream(stream);
        }
    }
}