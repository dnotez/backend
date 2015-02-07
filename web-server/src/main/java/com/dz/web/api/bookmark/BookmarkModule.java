package com.dz.web.api.bookmark;

import com.google.inject.AbstractModule;

/**
 *  Configure Bookmark related bindings
 * @author mamad
 * @since 10/11/14.
 */
public class BookmarkModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(BookmarkService.class).to(BookmarkServiceImpl.class);
    }
}
