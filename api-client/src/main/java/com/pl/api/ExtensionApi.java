package com.pl.api;

import com.pl.dsl.IdResponse;
import com.pl.dsl.PagedRequest;
import com.pl.dsl.PagedResponse;
import com.pl.dsl.Pong;
import com.pl.dsl.note.Note;
import com.pl.dsl.note.NoteResult;
import com.pl.dsl.note.SuggestionResponse;
import com.pl.dsl.extension.GetByUrlRequest;
import com.pl.dsl.extension.SaveSOFAnswerResponse;
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
