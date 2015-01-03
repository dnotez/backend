package com.pl.api;

import com.pl.dsl.IdResponse;
import com.pl.dsl.PagedResponse;
import com.pl.dsl.Pong;
import com.pl.dsl.article.ArticleResult;
import com.pl.dsl.cli.CliSearchRequest;
import com.pl.dsl.cli.SaveCmdRequest;
import com.pl.dsl.cli.SaveCmdResponse;
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
     * Fetch and article by type.
     * <p>
     * <code>
     * http://localhost:5050/cli/cmd/update_docker?k=label&n=2
     * </code>
     *
     * @param key     key for the fetching
     * @param keyType type of the key, default is 'id', could be 'id' or 'label'
     * @param num     number of articles to return, default is 1
     * @return the article resource
     */
    @GET("/cli/cmd/{key}")
    PagedResponse<ArticleResult> getArticle(@Path("key") String key, @Query("k") String keyType, @Query("n") int num);

    @POST("/cli/cmd")
    SaveCmdResponse saveCmd(@Body SaveCmdRequest request);

    @DELETE("/cli/cmd/{id}")
    IdResponse deleteItem(@Path("id") String id);

    @POST("/cli/search")
    PagedResponse<ArticleResult> search(@Body CliSearchRequest request);

}
