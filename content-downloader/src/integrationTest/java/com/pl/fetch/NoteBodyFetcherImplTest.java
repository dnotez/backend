package com.pl.fetch;

import com.pl.OkClientBuilder;
import com.squareup.okhttp.OkHttpClient;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

/**
 * @author mamad
 * @since 16/12/14.
 */
public class NoteBodyFetcherImplTest {
    @Test
    public void testFetchRedirect() throws Exception {
        OkHttpClient client = OkClientBuilder.create().get();
        NoteBodyFetcherImpl fetcher = new NoteBodyFetcherImpl(client);
        FetchResponse response = fetcher.fetchBody("http://httpbin.org/redirect-to?url=http://example.com/");
        verifyResponse(response);

        //now get https version, both https
        response = fetcher.fetchBody("https://httpbin.org/redirect-to?url=https://example.com/");
        verifyResponse(response);

        //redirect is http
        response = fetcher.fetchBody("http://httpbin.org/redirect-to?url=https://example.com/");
        verifyResponse(response);

        //target is http
        response = fetcher.fetchBody("https://httpbin.org/redirect-to?url=http://example.com/");
        verifyResponse(response);

        response = fetcher.fetchBody(null);
        assertNotNull(response);
        assertNull(response.getBody());
        assertEquals(response.getStatus(), -1);

    }

    protected void verifyResponse(FetchResponse response) {
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertThat(response.getStatus(), equalTo(200));
        assertTrue(response.getBody().contains("<title>Example Domain</title>"));
        assertTrue(response.getBody().contains("<h1>Example Domain</h1>"));
    }
}
