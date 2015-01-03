package com.pl.web;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import ratpack.exec.Fulfiller;
import ratpack.handling.Context;
import ratpack.http.TypedData;

import java.lang.reflect.Type;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Helper class for working with requests and responses in handlers.
 *
 * @author mamad
 * @since 13/12/14.
 */
@Singleton
public class HandlerHelper {
    private final Gson gson;

    @Inject
    public HandlerHelper(Gson gson) {
        this.gson = gson;
    }

    public <T> T fromBody(Context context, Class<T> type) {
        return fromBody(context, (Type) type);
    }

    public <T> T fromBody(Context context, Type type) {
        TypedData body = context.getRequest().getBody();
        T request = gson.fromJson(body.getText(), type);

        /**
         * enable leak detection: -Dio.netty.leakDetectionLevel=advanced or call  ResourceLeakDetector.setLevel()
         * see http://stackoverflow.com/a/25856285
         *
         * IMPORTANT: call release after every access to request body, otherwise there will be memory leak.
         * see http://netty.io/wiki/reference-counted-objects.html
         */
        body.getBuffer().release();
        return request;
    }

    public <T> Consumer<T> jsonConsumer(Fulfiller<String> fulfiller) {
        return response -> fulfiller.success(gson.toJson(response));
    }

    public <T, R> Consumer<T> jsonConsumer(Fulfiller<String> fulfiller, Function<T, R> mapper) {
        return response -> fulfiller.success(gson.toJson(mapper
                .apply(response)));
    }
}
