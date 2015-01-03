package com.pl.extraction;

import java.io.Serializable;
import java.util.Map;

/**
 * @author mamad
 * @since 22/11/14.
 */
public interface DetectedMimeType extends Serializable {
    /**
     * Return only type/sub-type of the mime. All the parameters will be removed.
     * To get full mime-type, use <code>fullMime()</code>
     *
     * @return mime-type in format "type/sub-type"
     */
    String asString();

    String fullMime();

    boolean hasParameters();

    Map<String, String> getParameters();
}
