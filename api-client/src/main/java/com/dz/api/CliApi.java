package com.dz.api;

import com.dz.dsl.IdResponse;
import com.dz.dsl.PagedResponse;
import com.dz.dsl.Pong;
import com.dz.dsl.cli.CliSearchRequest;
import com.dz.dsl.cli.SaveCmdRequest;
import com.dz.dsl.cli.SaveCmdResponse;
import com.dz.dsl.note.NoteResult;
import retrofit.http.*;

/**
 * The methods defined here are used by command line application.
 * This interface is only used by integration tests, as there is no java-based client for CliApi yet.
 *
 * @author mamad
 * @since 23/11/14.
 */
public interface CliApi {

    @GET("/api/ping")
    Pong ping();

    /**
     * Fetch and note by type.
     * <p>
     * <code>
     * http://localhost:5050/cli/cmd/update_docker?k=label&n=2
     * </code>
     *
     * @param key     key for the fetching
     * @param keyType type of the key, default is 'id', could be 'id' or 'label'
     * @param num     number of articles to return, default is 1
     * @return the note resource
     */
    @GET("/cli/cmd/{key}")
    PagedResponse<NoteResult> getNote(@Path("key") String key, @Query("k") String keyType, @Query("n") int num);

    @POST("/cli/cmd")
    SaveCmdResponse saveCmd(@Body SaveCmdRequest request);

    @DELETE("/cli/cmd/{id}")
    IdResponse deleteItem(@Path("id") String id);

    @POST("/cli/search")
    PagedResponse<NoteResult> search(@Body CliSearchRequest request);

}
