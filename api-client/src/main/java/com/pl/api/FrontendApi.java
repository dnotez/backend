package com.pl.api;


import com.pl.dsl.*;
import com.pl.dsl.note.Note;
import com.pl.dsl.note.NoteResult;
import com.pl.dsl.category.Category;
import retrofit.http.*;

import java.util.List;

/**
 * @deprecated The REST methods are not properly defined. Use NotesApi instead.
 *
 * The methods defined here are used by client-side javascript application.
 * @author mamad
 * @since 10/11/14.
 */
@Deprecated
public interface FrontendApi {

    @GET("/api/ping")
    Pong ping();

    /**
     * Returns list of categories for the current user.
     *
     * @return list of categories
     */
    @GET("/api/category/list")
    List<Category> getCategories();

    /**
     * Get the Note resource by id
     *
     * @return a resource response contains the code note for the given id with extra meta data if requested.
     */
    @GET("/api/article/{id}")
    ResourceResponse<Note> getArticle(@Path("id") String id);

    @POST("/api/article")
    IdResponse newArticle(@Body Note request);

    @PUT("/api/article/{id}")
    IdResponse updateArticle(@Path("id") String id, Note request);

    @DELETE("/api/article/{id}")
    IdResponse deleteArticle(@Path("id") String id);

    //deprecated see issues #26
    @POST("/api/article/list")
    PagedResponse<NoteResult> listArticles(@Body PagedRequest request);

    //see issues #26
    @GET("/api/articles")
    PagedResponse<NoteResult> listArticles(@Query("startIndex") int startIndex, @Query("pageSize") int pageSize, @Query("query") String query);

}
