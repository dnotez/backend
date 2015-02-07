package com.dz.dsl;

/**
 * @author mamad
 * @since 13/11/14.
 */
public class ResourceResponse<R> {
    private final R resource;

    public ResourceResponse(R resource) {
        this.resource = resource;
    }

    public R getResource() {
        return resource;
    }
}
