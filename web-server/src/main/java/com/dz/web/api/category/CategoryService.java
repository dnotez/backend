package com.dz.web.api.category;

import com.dz.dsl.category.Category;

import java.util.List;

/**
 * @author mamad
 * @since 01/11/14.
 */
public interface CategoryService {
    List<Category> getCategories();
}
