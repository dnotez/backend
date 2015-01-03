package com.pl.dsl;

import com.google.common.collect.ImmutableList;

import java.util.Arrays;
import java.util.stream.Collectors;

import static com.google.common.collect.ImmutableList.copyOf;

/**
 * @author mamad
 * @since 18/12/14.
 */
public class GetByKeyRequest {
    private String key;
    private KeyType keyType;
    private int count;

    public GetByKeyRequest(String key, int count, KeyType keyType) {
        this.key = key;
        this.count = count;
        this.keyType = keyType;
    }

    public GetByKeyRequest() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public KeyType getKeyType() {
        return keyType;
    }

    public void setKeyType(KeyType keyType) {
        this.keyType = keyType;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public static enum KeyType {
        ID,
        LABEL;

        public static final ImmutableList<String> STRING_VALUES = copyOf(Arrays.asList(values())
                .stream()
                .map(Enum::name)
                .collect(Collectors.toList()));
    }
}
