package com.dz.fetch;

import java.net.HttpURLConnection;

/**
 * @author mamad
 * @since 16/12/14.
 */
public class FetchResponse {
    private final String body;
    private final int status;

    public FetchResponse(int status, String body) {
        this.status = status;
        this.body = body;
    }

    public static FetchResponse create(int status, String body) {
        return new FetchResponse(status, body);
    }

    public String getBody() {
        return body;
    }

    public int getStatus() {
        return status;
    }

    public boolean isValid() {
        return status == HttpURLConnection.HTTP_OK;
    }
}
