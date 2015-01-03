package com.pl.web.cli;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.pl.dsl.cli.SaveCmdRequest;

/**
 * @author mamad
 * @since 13/12/14.
 */
@Singleton
public class SaveCmdUrlBuilderImpl implements SaveCmdUrlBuilder {
    public static final String BASE_URL = "BASE_URL";
    private final String baseUrl;

    @Inject
    public SaveCmdUrlBuilderImpl(@Named(BASE_URL) String baseUrl) {
        this.baseUrl = Preconditions.checkNotNull(baseUrl);
    }

    @Override
    public String urlOf(SaveCmdRequest request, String id) {
        return String.format("%s/cmd/%s", baseUrl, id);
    }
}
