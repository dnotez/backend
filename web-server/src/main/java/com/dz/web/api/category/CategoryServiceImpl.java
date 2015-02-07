package com.dz.web.api.category;

import com.dz.dsl.category.Category;
import com.google.common.collect.ImmutableList;

import java.util.List;

import static com.dz.dsl.category.Category.create;

/**
 * @author mamad
 * @since 01/11/14.
 */
public class CategoryServiceImpl implements CategoryService {
    private final List<Category> categories = ImmutableList.of(create("1", "Articles"), create("2", "Notes"),
            create("3", "Bookmarks"));

    @Override
    public List<Category> getCategories() {
        return categories;
    }
}
