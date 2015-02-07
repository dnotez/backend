package com.dz.dsl.bookmark;

/**
 * @author mamad
 * @since 10/11/14.
 */
public class Bookmark {
    private String id;
    private String url;
    private Bookmark bookmark;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Bookmark getBookmark() {
        return bookmark;
    }

    public void setBookmark(Bookmark bookmark) {
        this.bookmark = bookmark;
    }
}
