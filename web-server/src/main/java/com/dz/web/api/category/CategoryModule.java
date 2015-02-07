package com.dz.web.api.category;

import com.google.inject.AbstractModule;

/**
 * Configure Category related bindings
 * @author mamad
 * @since 10/11/14.
 */
public class CategoryModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(CategoryService.class).to(CategoryServiceImpl.class);
    }
}
