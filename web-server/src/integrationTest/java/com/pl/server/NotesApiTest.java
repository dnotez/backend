package com.pl.server;

import com.pl.api.NotesApi;
import com.pl.client.ApiClientBuilder;
import com.pl.dsl.IdResponse;
import com.pl.dsl.PagedRequest;
import com.pl.dsl.PagedResponse;
import com.pl.dsl.ResourceResponse;
import com.pl.dsl.note.Note;
import com.pl.dsl.note.NoteFields;
import com.pl.dsl.note.NoteResult;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

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
    public void testAllApiMethods() throws Exception {
        esHelper.deleteAllDocs().ensureGreen();

        String user1 = "user1";
        Note n1 = newNote("1");
        IdResponse response = notesApi.create(user1, n1);
        verifyIdResponse(response);

        ResourceResponse<Note> resourceResponse = notesApi.get(user1, "1");
        verifyGetResponse("1", n1, resourceResponse);

        //second one
        Note n2 = newNote("2");
        response = notesApi.create(user1, n2);
        verifyIdResponse(response);
        resourceResponse = notesApi.get(user1, "2");
        verifyGetResponse("2", n2, resourceResponse);

        esHelper.ensureIndexed(2).ensureGreen();

        //list
        PagedResponse<NoteResult> listResponse = notesApi.list(user1, 0, 10);
        verifyListResponse(listResponse, 2, 2);

        listResponse = notesApi.list(user1, 0, 1);
        verifyListResponse(listResponse, 2, 1);

        //delete first one, id is assigned by server
        response = notesApi.delete(user1, listResponse.getResults().get(0).getItem().getId());
        verifyIdResponse(response);

        esHelper.ensureIndexed(1, true).ensureGreen();

        listResponse = notesApi.list(user1, 0, 2);
        verifyListResponse(listResponse, 1, 1);

        //update note
        Note updated = newNote("3");
        updated.setId(listResponse.getResults().get(0).getItem().getId());
        response = notesApi.update(user1, updated.getId(), updated);
        verifyIdResponse(response);

        resourceResponse = notesApi.get(user1, listResponse.getResults().get(0).getItem().getId());
        verifyGetResponse(updated.getId(), updated, resourceResponse);

        //need to wait for ES to update the item
        esHelper.ensureIndexed(1, true, QueryBuilders.multiMatchQuery(updated.getTitle(), NoteFields.TITLE)).ensureGreen();

        listResponse = notesApi.search(user1, PagedRequest.create(updated.getTitle()));
        verifyListResponse(listResponse, 1, 1);
    }

    private void verifyListResponse(PagedResponse<NoteResult> listResponse, long expectedTotal, int expectedSize) {
        assertNotNull(listResponse);
        List<NoteResult> results = listResponse.getResults();
        assertNotNull(results);
        assertThat(listResponse.getTotal(), equalTo(expectedTotal));
        assertThat(results.size(), equalTo(expectedSize));
        for (NoteResult result : results) {
            assertNotNull(result.getItem());
        }
    }

    private void verifyGetResponse(String id, Note request, ResourceResponse<Note> resourceResponse) {
        assertNotNull(resourceResponse);
        Note note = resourceResponse.getResource();
        assertNotNull(note);
        assertThat(note.getId(), equalTo(id));
        assertThat(note.getBody(), equalTo(request.getBody()));
        assertThat(note.getTitle(), equalTo(request.getTitle()));
    }

    private void verifyIdResponse(IdResponse response) {
        assertNotNull(response);
        assertFalse(response.isError());
        assertNotNull(response.getId());
    }

    private Note newNote(String id) {
        return new Note(id, "url" + id, "title" + id, "body" + id);
    }
}
