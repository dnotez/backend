package com.dz.web.api.category;

/**
 * @author mamad
 * @since 01/11/14.
 */

import com.google.gson.Gson;
import com.google.inject.Inject;
import ratpack.func.Action;
import ratpack.handling.Chain;

import javax.inject.Singleton;

/**
 * A handler implementation that is created via dependency injection.
 *
 */
@Singleton
public class CategoryHandler implements Action<Chain> {

    private final CategoryService categoryService;
    private final Gson gson;

    @Inject
    public CategoryHandler(CategoryService categoryService, Gson gson) {
        this.categoryService = categoryService;
        this.gson = gson;
    }

    @Override
    public void execute(Chain chain) throws Exception {
        chain.handler("list", context -> context.render(gson.toJson(categoryService.getCategories())));
    }
}