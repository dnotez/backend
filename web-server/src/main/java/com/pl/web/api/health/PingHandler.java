package com.pl.web.api.health;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.pl.dsl.Pong;
import ratpack.handling.Context;
import ratpack.handling.Handler;

/**
 * @author mamad
 * @since 19/11/14.
 */
@Singleton
public class PingHandler implements Handler {
    private final Gson gson;

    @Inject
    public PingHandler(Gson gson) {
        this.gson = gson;
    }

    @Override
    public void handle(Context context) throws Exception {
        context.render(gson.toJson(new Pong(true)));
    }
}
