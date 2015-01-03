package com.pl.web.cli;

import com.google.common.base.Strings;
import com.google.inject.Singleton;
import com.pl.dsl.cli.SaveCmdRequest;

/**
 * @author mamad
 * @since 16/12/14.
 */
@Singleton
public class SaveCmdHtmlBodyBuilderImpl implements SaveCmdHtmlBodyBuilder {
    @Override
    public String bodyOf(SaveCmdRequest request) {
        String body = Strings.nullToEmpty(request.getBody());
        return String.format("<pre><code>%s</code></pre>", body);
    }
}
