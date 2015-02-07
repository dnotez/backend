package com.dz.web;

import io.netty.handler.codec.http.HttpResponseStatus;
import ratpack.handling.Context;
import ratpack.http.internal.DefaultStatus;

import static com.google.common.base.Throwables.getStackTraceAsString;
import static java.net.HttpURLConnection.*;

/**
 * Helper class for sending HTTP status code and messages to the server.
 *
 * @author mamad
 * @since 16/11/14.
 */
public class StatusHelper {

    public static void sendNotFound(Context context, String id) {
        sendStatus(context, new HttpResponseStatus(HTTP_NOT_FOUND, "There is not item with id:" + id));
    }

    public static void sendStatus(Context context, String reasonPhrase, int code) {
        sendStatus(context, new HttpResponseStatus(code, reasonPhrase));
    }

    public static void sendStatus(Context context, HttpResponseStatus status) {
        context.getResponse().status(new DefaultStatus(status)).send();
    }

    public static void sendBadRequest(Context context, String message) {
        sendStatus(context, new HttpResponseStatus(HTTP_BAD_REQUEST, message));
    }

    public static void sendInternalError(Context context, Throwable throwable) {
        sendStatus(context, new HttpResponseStatus(HTTP_INTERNAL_ERROR, "Unknown error:" + getStackTraceAsString(throwable)));
    }
}
