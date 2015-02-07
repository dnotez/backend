package com.dz.dsl.category;

import com.google.common.base.Strings;

import java.util.Objects;

/**
 * @author mamad
 * @since 10/11/14.
 */
public class Category {
    private final String id;
    private final String name;

    public Category(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public static Category create(String id, String name) {
        return new Category(id, name);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Category)) {
            return false;
        }
        Category category = (Category) o;
        return Objects.equals(id, category.id);
    }

    @Override
    public int hashCode() {
        return Strings.nullToEmpty(id).hashCode();
    }
}
