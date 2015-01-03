package com.pl;

import com.squareup.okhttp.OkHttpClient;

/**
 * @author mamad
 * @since 16/12/14.
 */
public class OkClientBuilder {
    public static OkClientBuilder create() {
        return new OkClientBuilder();
    }

    public OkHttpClient get() {
        return new OkHttpClient();
    }
}
