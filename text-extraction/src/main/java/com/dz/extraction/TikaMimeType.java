package com.dz.extraction;

import org.apache.tika.mime.MediaType;

import java.util.Map;

/**
 * A wrapper around Tika MediaType class
 *
 * @author mamad
 * @since 22/11/14.
 */
public class TikaMimeType implements DetectedMimeType {
    private final MediaType mediaType;

    public TikaMimeType(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    @Override
    public String asString() {
        return mediaType.getType() + "/" + mediaType.getSubtype();
    }

    @Override
    public boolean hasParameters() {
        return mediaType.hasParameters();
    }

    @Override
    public String fullMime() {
        return mediaType.toString();
    }

    @Override
    public Map<String, String> getParameters() {
        return mediaType.getParameters();
    }
}
