package com.pl.web.cli;

import com.pl.dsl.cli.SaveCmdRequest;

/**
 * @author mamad
 * @since 16/12/14.
 */
public interface SaveCmdHtmlBodyBuilder {
    String bodyOf(SaveCmdRequest request);
}
