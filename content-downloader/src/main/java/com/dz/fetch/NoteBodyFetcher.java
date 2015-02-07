package com.dz.fetch;

import java.io.IOException;

/**
 * @author mamad
 * @since 15/12/14.
 */
public interface NoteBodyFetcher {
    FetchResponse fetchBody(String url) throws IOException;
}
