package com.pl.web;

import com.google.common.base.Preconditions;
import ratpack.util.MultiValueMap;

/**
 * Helper class for working with Raptack's request query params
 *
 * @author mamad
 * @since 16/12/14.
 */
public class QueryParams {
    private final MultiValueMap<String, String> params;

    public QueryParams(MultiValueMap<String, String> params) {
        this.params = params;
    }

    public static QueryParams create(MultiValueMap<String, String> params) {
        return new QueryParams(params);
    }

    public String valueOf(String key, String defaultValue) {
        Preconditions.checkNotNull(key, "key can not be null.");
        return params.containsKey(key) ? params.get(key) : defaultValue;
    }

    public int valueOf(String key, int defaultValue) {
        try {
            return Integer.parseInt(valueOf(key, Integer.toString(defaultValue)));
        } catch (NumberFormatException ignored) { // NOSONAR
            return defaultValue;
        }
    }
}
