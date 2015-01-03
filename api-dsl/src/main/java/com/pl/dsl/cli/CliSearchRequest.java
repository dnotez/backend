package com.pl.dsl.cli;

/**
 * This is a wrapper around search request submitted from command line interface (see cli, go lang impl.)
 * The result of this request is <strong>PagedResponse&lt;ArticleResult&gt;</strong>
 * <p>
 * check com.pl.web.cli.CliApiHandler
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
