package com.dz.api;

import com.dz.dsl.IdResponse;
import com.dz.dsl.PagedRequest;
import com.dz.dsl.PagedResponse;
import com.dz.dsl.Pong;
import com.dz.dsl.extension.GetByUrlRequest;
import com.dz.dsl.extension.SaveSOFAnswerResponse;
import com.dz.dsl.note.Note;
import com.dz.dsl.note.NoteResult;
import com.dz.dsl.note.SuggestionResponse;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;

/**
 * The methods defined here are used by Chrome extension.
 *
 * @author mamad
 * @since 23/11/14.
 */
public interface ExtensionApi {

    @GET("/api/ping")
    Pong ping();

    @POST("/extension/sof/answer")
    SaveSOFAnswerResponse saveStackOverflowAnswer(@Body Note request);


    @POST("/extension/page")
    IdResponse savePage(@Body Note request);

    @POST("/extension/selected")
    IdResponse saveSelectedText(@Body Note request);


    @POST("/extension/check")
    PagedResponse<NoteResult> isAlreadySaved(@Body GetByUrlRequest request);

    @POST("/extension/suggestion")
    SuggestionResponse suggestion(@Body PagedRequest request);
}
