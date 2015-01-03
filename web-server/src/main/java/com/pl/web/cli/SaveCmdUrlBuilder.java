package com.pl.web.cli;

import com.pl.dsl.cli.SaveCmdRequest;

/**
 * @author mamad
 * @since 13/12/14.
 */
public interface SaveCmdUrlBuilder {
    String urlOf(SaveCmdRequest request, String id);
}
