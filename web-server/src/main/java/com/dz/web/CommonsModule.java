package com.dz.web;

import com.dz.dsl.GsonHelper;
import com.google.gson.Gson;
import com.google.inject.AbstractModule;

/**
 * @author mamad
 * @since 19/11/14.
 */
public class CommonsModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(Gson.class).toInstance(GsonHelper.create().buildDefault());
    }
}
