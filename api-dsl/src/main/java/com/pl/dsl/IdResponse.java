package com.pl.dsl;

import com.google.common.base.Preconditions;

/**
 * A generic response object used for saving/updating/deleting objects.
 *
 * @author mamad
 * @since 13/11/14.
 */
public class IdResponse extends GeneralResponse<IdResponse> {
    private final String id;

    IdResponse(String id) {
        this.id = id;
    }

    public static IdResponse create(String id) {
        Preconditions.checkArgument(id != null && !id.isEmpty(), "id can not be null or empty.");
        return new IdResponse(id);
    }

    @Override
    public IdResponse error(String message) {
        return new IdResponse(message);
    }

    public String getId() {
        return id;
    }
}
