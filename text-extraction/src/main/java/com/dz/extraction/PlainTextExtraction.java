package com.dz.extraction;

/**
 * Text extraction response
 *
 * @author mamad
 * @since 22/11/14.
 */
public class PlainTextExtraction {
    private DetectedMimeType mimeType;
    private String plainText;

    public PlainTextExtraction() {
    }

    public PlainTextExtraction(DetectedMimeType mimeType, String plainText) {
        this.mimeType = mimeType;
        this.plainText = plainText;
    }

    public String getPlainText() {
        return plainText;
    }

    public void setPlainText(String plainText) {
        this.plainText = plainText;
    }

    public DetectedMimeType getMimeType() {
        return mimeType;
    }

    public void setMimeType(DetectedMimeType mimeType) {
        this.mimeType = mimeType;
    }
}
