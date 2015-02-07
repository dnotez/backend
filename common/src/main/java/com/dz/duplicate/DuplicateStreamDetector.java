package com.dz.duplicate;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author mamad
 * @since 22/11/14.
 */
public interface DuplicateStreamDetector {
    String hash(InputStream stream) throws IOException;
    boolean isDuplicate(InputStream newStream, InputStream oldStream) throws IOException;
}
