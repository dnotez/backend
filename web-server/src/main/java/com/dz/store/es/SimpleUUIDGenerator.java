package com.dz.store.es;

import java.util.UUID;

/**
 * @author mamad
 * @since 13/11/14.
 */
public class SimpleUUIDGenerator implements UUIDGenerator {
    @Override
    public String newId() {
        return UUID.randomUUID().toString();
    }
}
