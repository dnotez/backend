package com.dz.dsl.cli;

/**
 * This is a wrapper around search request submitted from command line interface (see cli, go lang impl.)
 * The result of this request is <strong>PagedResponse&lt;NoteResult&gt;</strong>
 * <p>
 * check com.dz.web.cli.CliApiHandler
 *
 * @author mamad
 * @since 13/12/14.
 */
public class CliSearchRequest {
    private String label;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
