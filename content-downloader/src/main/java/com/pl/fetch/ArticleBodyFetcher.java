package com.pl.fetch;

import java.io.IOException;

/**
 * @author mamad
 * @since 15/12/14.
 */
public interface ArticleBodyFetcher {
    FetchResponse fetchBody(String url) throws IOException;
}
