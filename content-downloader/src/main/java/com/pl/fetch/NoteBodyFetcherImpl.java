package com.pl.fetch;

import com.google.inject.Inject;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * @author mamad
 * @since 16/12/14.
 */
public class NoteBodyFetcherImpl implements NoteBodyFetcher {
    private final OkHttpClient okClient;

    @Inject
    public NoteBodyFetcherImpl(OkHttpClient okClient) {
        this.okClient = okClient;
    }

    @Override
    public FetchResponse fetchBody(String url) throws IOException {
        if (url == null || url.length() < 1) {
            return FetchResponse.create(-1, null);
        }

        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = okClient.newCall(request).execute();
        String responseSource = response.networkResponse() != null
                ? ("(network: " + response.networkResponse().code() + ")")
                : "(cache)";
        int responseCode = response.code();

        System.out.printf("%03d: %s %s%n", responseCode, url, responseSource);

        String contentType = response.header("Content-Type");
        if (responseCode != 200 || contentType == null) {
            response.body().close();
            return FetchResponse.create(responseCode, null);
        }

        return FetchResponse.create(responseCode, response.body().string());
    }
}
