package com.pl.server;

import com.pl.api.FrontendApi;
import com.pl.client.ApiClientBuilder;
import com.pl.dsl.*;
import com.pl.dsl.note.Note;
import com.pl.dsl.note.NoteResult;
import com.pl.store.es.IndexName;
import org.hamcrest.Matchers;
import org.junit.Test;
import retrofit.RetrofitError;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

/**
 * @author mamad
 * @since 10/11/14.
 */
public class FrontendApiTest extends BaseApiTest {

    protected FrontendApi frontendApi;

    @Override
    protected void prepareApi(String url) {
        ApiClientBuilder apiClientBuilder = ApiClientBuilder.create().withUrl(url);
        frontendApi = apiClientBuilder.forntendApi();
    }

    @Test
    public void testPing() throws Exception {
        Pong pong = frontendApi.ping();
        assertNotNull(pong);
        assertTrue(pong.isOkay());
    }

    @Test
    public void testFrontendApi() throws Exception {
        esHelper.wipeIndex()
                .ensureIndexed(0);
        Note note = new Note("", "url1", "title1", "body1");
        IdResponse createResponse = frontendApi.newArticle(note);
        assertNotNull(createResponse);
        assertNotNull(createResponse.getId());
        assertFalse(createResponse.getId().isEmpty());
        ResourceResponse<Note> response = frontendApi.getArticle(createResponse.getId());
        assertNotNull(response);
        assertNotNull(response.getResource());
        assertThat(response.getResource().getId(), equalTo(createResponse.getId()));
        assertThat(response.getResource().getUrl(), equalTo("url1"));
        assertThat(response.getResource().getTitle(), equalTo("title1"));
        assertThat(response.getResource().getBody(), equalTo("body1"));
        assertThat(response.getResource().getText(), equalTo("body1"));
        assertThat(response.getResource().getMimeType(), equalTo("text/html"));
        assertNotNull(response.getResource().getMd5());
        assertThat(response.getResource().getSaveDate(), Matchers.greaterThan(0L));

        //get random item
        String randomID = UUID.randomUUID().toString();
        try {
            frontendApi.getArticle(randomID);
            fail("Should throw an exception");
        } catch (Exception e) {
            assertTrue(e instanceof RetrofitError);
            assertEquals(RetrofitError.Kind.HTTP, ((RetrofitError) e).getKind());
            assertEquals(404, ((RetrofitError) e).getResponse().getStatus());
        }

        ensureSaved(IndexName.MAIN, 1);

        //get list of all
        PagedRequest request = new PagedRequest();
        PagedResponse<NoteResult> listResponse = frontendApi.listArticles(request);
        assertNotNull(listResponse);
        assertThat(listResponse.getTotal(), equalTo(1L));
        List<NoteResult> items = listResponse.getResults();
        assertNotNull(items);
        assertThat(items.size(), equalTo(1));
        Result<Note> result = items.get(0);
        assertNotEquals(0, result.getScore());
        assertNotNull(result.getItem());
        assertThat(result.getItem().getId(), equalTo(createResponse.getId()));
        assertThat(result.getItem().getUrl(), equalTo("url1"));
        assertThat(result.getItem().getBody(), equalTo("body1"));
        assertThat(result.getItem().getTitle(), equalTo("title1"));
    }

}
