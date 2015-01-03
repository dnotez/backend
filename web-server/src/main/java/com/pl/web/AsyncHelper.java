package com.pl.web;

import com.pl.bean.InvalidValueException;
import com.pl.store.es.ArticleNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.func.Action;
import ratpack.handling.Context;

import java.net.HttpURLConnection;

/**
 * @author mamad
 * @since 23/11/14.
 */
public class AsyncHelper {
    public static final Logger LOGGER = LoggerFactory.getLogger(AsyncHelper.class);

    public static Action<String> newJsonAction(Context context) {
        return newJsonAction(context, HttpURLConnection.HTTP_OK);
    }

    public static Action<String> newJsonAction(Context context, int statusCode) {
        return resultJson -> context.getResponse()
                .status(statusCode)
                .send("application/json", resultJson);
    }

    public static Action<Throwable> newErrorAction(Context context) {
        return e -> {
            LOGGER.error("Error in handling request", e);
            if (e instanceof ArticleNotFoundException) {
                StatusHelper.sendNotFound(context, ((ArticleNotFoundException) e).getId());
            } else if (e instanceof InvalidValueException) {
                StatusHelper.sendBadRequest(context, e.getMessage());
            } else {
                StatusHelper.sendInternalError(context, e);
            }
        };
    }
}
