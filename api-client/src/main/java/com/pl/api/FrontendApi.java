package com.pl.api;


import com.pl.dsl.*;
import com.pl.dsl.article.Article;
import com.pl.dsl.article.ArticleResult;
import com.pl.dsl.category.Category;
import retrofit.http.*;

import java.util.List;

/**
 * The methods defined here are used by client-side javascript application.
 * @author mamad
 * @since 10/11/14.
 */
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
     * Get the Article resource by id
     *
     * @return a resource response contains the code article for the given id with extra meta data if requested.
     */
    @GET("/api/article/{id}")
    ResourceResponse<Article> getArticle(@Path("id") String id);

    @POST("/api/article")
    IdResponse newArticle(@Body Article request);

    @PUT("/api/article/{id}")
    IdResponse updateArticle(@Path("id") String id, Article request);

    @DELETE("/api/article/{id}")
    IdResponse deleteArticle(@Path("id") String id);

    //deprecated see issues #26
    @POST("/api/article/list")
    PagedResponse<ArticleResult> listArticles(@Body PagedRequest request);

    //see issues #26
    @GET("/api/articles")
    PagedResponse<ArticleResult> listArticles(@Query("startIndex") int startIndex, @Query("pageSize") int pageSize, @Query("query") String query);

}
