package com.dz.media;

/**
 * @author mamad
 * @since 07/02/15.
 */
public class MediaExtractionRequest {
    private final String html;
    private boolean extractImages = true;
    private boolean extractVideos = true;

    public MediaExtractionRequest(String html) {
        this.html = html;
    }

    public static MediaExtractionRequest create(String html) {
        return new MediaExtractionRequest(html);
    }

    public boolean isExtractImages() {
        return extractImages;
    }

    public void setExtractImages(boolean extractImages) {
        this.extractImages = extractImages;
    }

    public boolean isExtractVideos() {
        return extractVideos;
    }

    public void setExtractVideos(boolean extractVideos) {
        this.extractVideos = extractVideos;
    }

    public String getHtml() {
        return html;
    }
}
