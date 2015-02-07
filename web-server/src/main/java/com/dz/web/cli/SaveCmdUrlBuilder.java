package com.dz.web.cli;

import com.dz.dsl.cli.SaveCmdRequest;

/**
 * @author mamad
 * @since 13/12/14.
 */
public interface SaveCmdUrlBuilder {
    String urlOf(SaveCmdRequest request, String id);
}
