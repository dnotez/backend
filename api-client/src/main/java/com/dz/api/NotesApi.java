package com.dz.api;

import com.dz.dsl.IdResponse;
import com.dz.dsl.PagedRequest;
import com.dz.dsl.PagedResponse;
import com.dz.dsl.ResourceResponse;
import com.dz.dsl.note.Note;
import com.dz.dsl.note.NoteResult;
import retrofit.http.*;

/**
 * @author mamad
 * @since 20/01/15.
 */
public interface NotesApi {
    /**
     * List user's notes.
     *
     * @param user       name of the user
     * @param startIndex start index of the notes. default is 0
     * @param pageSize   number of notes in the response, default is 10
     * @return a paged response contains total number and list of notes for the current page
     */
    @GET("/users/{user}/notes")
    PagedResponse<NoteResult> list(@Path("user") String user, @Query("startIndex") int startIndex,
                                   @Query("pageSize") int pageSize);

    @POST("/users/{user}/notes")
    IdResponse create(@Path("user") String user, @Body Note request);

    @POST("/users/{user}/notes/search")
    PagedResponse<NoteResult> search(@Path("user") String user, @Body PagedRequest request);

    @GET("/users/{user}/notes/{id}")
    ResourceResponse<Note> get(@Path("user") String user, @Path("id") String id);

    @PUT("/users/{user}/notes/{id}")
    IdResponse update(@Path("user") String user, @Path("id") String id, @Body Note request);

    @DELETE("/users/{user}/notes/{id}")
    IdResponse delete(@Path("user") String user, @Path("id") String id);

}
