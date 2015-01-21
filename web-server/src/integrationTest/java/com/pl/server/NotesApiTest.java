package com.pl.server;

import com.pl.api.NotesApi;
import com.pl.client.ApiClientBuilder;
import com.pl.dsl.note.Note;
import org.junit.Test;

/**
 * @author mamad
 * @since 21/01/15.
 */
public class NotesApiTest extends BaseApiTest {
    private NotesApi notesApi;
    @Override
    protected void prepareApi(String url) {
        notesApi = ApiClientBuilder.create().withUrl(url).notesApi();
    }

    @Test
    public void testCreate() throws Exception {
        //Note request = new Note("id1", "url1", "title1", "body1");
        //notesApi.create("user1", request);

    }
}
