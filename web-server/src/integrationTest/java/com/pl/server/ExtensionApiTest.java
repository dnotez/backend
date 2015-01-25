package com.pl.server;

import com.pl.api.ExtensionApi;
import com.pl.client.ApiClientBuilder;
import com.pl.dsl.PagedRequest;
import com.pl.dsl.PagedResponse;
import com.pl.dsl.Pong;
import com.pl.dsl.extension.GetByUrlRequest;
import com.pl.dsl.note.NoteResult;
import com.pl.dsl.note.SuggestionResponse;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

/**
 * @author mamad
 * @since 01/12/14.
 */
public class ExtensionApiTest extends BaseApiTest {

    protected ExtensionApi extensionApi;

    @Override
    protected void prepareApi(String url) {
        ApiClientBuilder apiClientBuilder = ApiClientBuilder.create().withUrl(url);
        extensionApi = apiClientBuilder.extensionApi();
    }

    @Test
    public void testPing() throws Exception {
        Pong pong = extensionApi.ping();
        assertNotNull(pong);
        assertTrue(pong.isOkay());
    }

    @Test
    public void testIsAlreadySaved() throws Exception {
        PagedResponse<NoteResult> response = extensionApi.isAlreadySaved(GetByUrlRequest.of("url2"));
        verifyResponse(0, response);

        response = extensionApi.isAlreadySaved(GetByUrlRequest.of("http://stackoverflow.com/a/21023161"));
        List<NoteResult> results = verifyResponse(1, response);
        assertThat(results.get(0).getItem().getTitle(), equalTo("Copying local files with curl"));

    }

    protected List<NoteResult> verifyResponse(int size, PagedResponse<NoteResult> response) {
        assertNotNull(response);
        assertFalse(response.isError());
        assertNotNull(response.getResults());
        assertThat(response.getResults().size(), equalTo(size));
        return response.getResults();
    }

    @Test
    public void testSuggestion() throws Exception {
        esHelper.wipeIndex()
                .ensureIndexed(0);
        //no note indexed yet
        SuggestionResponse suggestion = extensionApi.suggestion(PagedRequest.create("prog"));
        assertNotNull(suggestion);
        assertFalse(suggestion.isError());
        assertNotNull(suggestion.getResults());
        assertTrue(suggestion.getResults().isEmpty());
        esHelper.indexSampleData().ensureIndexed();

        suggestion = extensionApi.suggestion(PagedRequest.create("cur"));
        assertNotNull(suggestion);
        assertFalse(suggestion.isError());
        assertNotNull(suggestion.getResults());
        assertThat(suggestion.getResults().size(), equalTo(2));
        assertThat(suggestion.getResults().get(0).getSuggestion(), equalTo("How do I get cURL to not show the progress bar?"));
        assertThat(suggestion.getResults().get(1).getSuggestion(), equalTo("Copying local files with curl"));
    }

    //todo: test other extension REST api method such as savePage and saveSelection
}
