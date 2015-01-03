package com.pl.web.api.category;

import com.google.common.collect.ImmutableList;
import com.pl.dsl.category.Category;

import java.util.List;

import static com.pl.dsl.category.Category.create;

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
