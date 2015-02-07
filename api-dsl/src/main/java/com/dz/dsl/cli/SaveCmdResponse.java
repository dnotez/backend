package com.dz.dsl.cli;

/**
 * A response for save command request
 *
 * @author mamad
 * @since 11/12/14.
 */
public class SaveCmdResponse {
    private String url;
    private String id;

    public SaveCmdResponse(String url, String id) {
        this.url = url;
        this.id = id;
    }

    public SaveCmdResponse() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
