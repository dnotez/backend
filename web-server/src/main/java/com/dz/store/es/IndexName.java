package com.dz.store.es;

import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * @author mamad
 * @since 14/11/14.
 */
public enum IndexName {
    MAIN("main", Type.NOTE, Type.CATEGORY),
    ACCOUNT("account", Type.USER, Type.TEAM, Type.ACCOUNT),;

    private final List<Type> types;
    private final String indexName;

    IndexName(String indexName, Type... types) {
        this.types = ImmutableList.copyOf(types);
        this.indexName = indexName;
    }

    public List<Type> getTypes() {
        return types;
    }

    public String indexName() {
        return indexName;
    }

    public static enum Type {
        //types for MAIN index
        NOTE,
        CATEGORY,

        //types for ACCOUNT index
        USER,
        TEAM,
        ACCOUNT,;

        public String typeName() {
            return name().toLowerCase();
        }
    }
}
