package com.dz.web.cli;

import com.dz.dsl.cli.SaveCmdRequest;

/**
 * @author mamad
 * @since 16/12/14.
 */
public interface SaveCmdHtmlBodyBuilder {
    String bodyOf(SaveCmdRequest request);
}
