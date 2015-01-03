package com.pl.web.cli;

import com.google.common.base.Preconditions;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

import static com.pl.web.cli.FirstLineSaveCmdTitleBuilder.MAX_TITLE_LENGTH;
import static com.pl.web.cli.FirstLineSaveCmdTitleBuilder.PREPEND_LABEL;

/**
 * @author mamad
 * @since 13/12/14.
 */
public class CliModule extends AbstractModule {
    private final String url;

    public CliModule(String url) {
        this.url = Preconditions.checkNotNull(url, "URL can not be null.");
    }

    @Override
    protected void configure() {
        bind(SaveCmdPersister.class).to(SaveCmdPersisterImpl.class);

        bindConstant().annotatedWith(Names.named(MAX_TITLE_LENGTH)).to(30);
        bindConstant().annotatedWith(Names.named(PREPEND_LABEL)).to(true);
        bind(SaveCmdTitleBuilder.class).to(FirstLineSaveCmdTitleBuilder.class);
        bind(SaveCmdHtmlBodyBuilder.class).to(SaveCmdHtmlBodyBuilderImpl.class);

        bindConstant().annotatedWith(Names.named(SaveCmdUrlBuilderImpl.BASE_URL)).to(url);
        bind(SaveCmdUrlBuilder.class).to(SaveCmdUrlBuilderImpl.class);
    }
}
