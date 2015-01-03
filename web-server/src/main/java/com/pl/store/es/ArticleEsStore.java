package com.pl.store.es;

import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.io.ByteSource;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.pl.bean.ArgChecker;
import com.pl.bean.InvalidValueException;
import com.pl.dsl.*;
import com.pl.dsl.article.Article;
import com.pl.dsl.article.ArticleFields;
import com.pl.dsl.article.ArticleResult;
import com.pl.dsl.article.SuggestionResponse;
import com.pl.dsl.extension.GetByUrlRequest;
import com.pl.duplicate.DuplicateStreamDetector;
import com.pl.store.es.StoreActionFailedException.Action;
import com.pl.time.DateTimeHelper;
import org.apache.commons.beanutils.BeanUtils;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.indices.IndexMissingException;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.pl.store.es.IndexName.MAIN;
import static com.pl.store.es.IndexName.Type.ARTICLE;

/**
 * @author mamad
 * @since 13/11/14.
 */
@Singleton
public class ArticleEsStore implements ArticleStore {
    public static final Logger LOGGER = LoggerFactory.getLogger(ArticleEsStore.class);
    private final Client client;
    private final UUIDGenerator uuidGenerator;
    private final ActionTimeouts actionTimeouts;
    private final EsQueryBuilderFactory queryBuilderFactory;
    private final DuplicateStreamDetector duplicateStreamDetector;
    private final ArticleSuggester articleSuggester;
    private final IndexableArticleComposer articleComposer;

    @Inject
    public ArticleEsStore(Client client, UUIDGenerator uuidGenerator, ActionTimeouts actionTimeouts,
                          EsQueryBuilderFactory queryBuilderFactory,
                          DuplicateStreamDetector duplicateStreamDetector,
                          ArticleSuggester articleSuggester, IndexableArticleComposer articleComposer) {
        this.client = client;
        this.uuidGenerator = uuidGenerator;
        this.actionTimeouts = actionTimeouts;
        this.queryBuilderFactory = queryBuilderFactory;
        this.duplicateStreamDetector = duplicateStreamDetector;
        this.articleSuggester = articleSuggester;
        this.articleComposer = articleComposer;
    }

    @Override
    public IdResponse save(Article article) throws IOException, StoreActionFailedException {
        String id;
        try {
            id = verify(article);
        } catch (DuplicateArticleException e) { // NOSONAR
            return IdResponse.create(e.getArticle().getId());
        } catch (InvalidValueException e) {
            throw new StoreActionFailedException(Action.CREATE, e.getMessage(), e);
        }
        try {
            XContentBuilder builder = articleComposer.compose(article);
            IndexResponse response = client
                    .prepareIndex(MAIN.indexName(), ARTICLE.typeName(), id)
                    .setSource(builder)
                    .execute()
                    .actionGet();
            if (response.isCreated()) {
                return IdResponse.create(id);
            } else {
                LOGGER.error("Article save failed, id:{}, url:{}", id, article.getUrl());
            }
        } catch (IOException e) {
            LOGGER.error("IO error while saving article with url:{}indexAndWait", article.getUrl(), e);
        } catch (ElasticsearchException e) {
            LOGGER.error("Elasticsearch error while saving article with url:{}", article.getUrl(), e);
        }
        throw StoreActionFailedException.createFailed(id);
    }

    private String verify(Article article) throws IOException, StoreActionFailedException, DuplicateArticleException, InvalidValueException {
        ArgChecker.create()
                .notNull(article, "article can not be null.")
                .notNull(article.getBody(), "article body can not be null.")
                .notNull(article.getUrl(), "article url can not be null.")
                .verify();
        InputStream stream = ByteSource.wrap(article.getBody().getBytes(Charsets.UTF_8)).openStream();
        String md5 = duplicateStreamDetector.hash(stream);

        //first check by md5 hash to avoid double save
        Optional<Article> md5Optional = findByMd5(md5);
        if (md5Optional.isPresent()) {
            LOGGER.debug("Another article with same id is already saved, saved article url:{}, new article url:{}",
                    md5Optional.get(), article.getUrl());
            throw new DuplicateArticleException(md5Optional.get());
        }

        String id = article.getId();
        if (Strings.isNullOrEmpty(id)) {
            id = uuidGenerator.newId();
            article.setId(id);
        }
        article.setMd5(md5);
        article.setSaveDate(DateTimeHelper.currentTimeInMs());

        return id;
    }

    @Override
    public void asyncSave(Article article, Consumer<IdResponse> onSuccess, Consumer<Throwable> onError) {
        String id;
        try {
            id = verify(article);
        } catch (DuplicateArticleException e) {
            onSuccess.accept(IdResponse.create(e.getArticle().getId()));
            return;
        } catch (StoreActionFailedException e) {
            LOGGER.error("Error in verifying article:{}", article, e);
            onError.accept(e);
            return;
        } catch (IOException e) {
            LOGGER.error("IO error while verifying article:{}", article, e);
            onError.accept(e);
            return;
        } catch (InvalidValueException e) { // NOSONAR
            LOGGER.error("article:{} is not valid, value:{}, message:{}.", article, e.getValue(), e.getMessage());
            onError.accept(e);
            return;
        }
        try {
            XContentBuilder builder = articleComposer.compose(article);
            client.prepareIndex(MAIN.indexName(), ARTICLE.typeName(), id)
                    .setSource(builder)
                    .execute()
                    .addListener(new ActionListener<IndexResponse>() {
                        @Override
                        public void onResponse(IndexResponse indexResponse) {
                            onSuccess.accept(IdResponse.create(id));
                        }

                        @Override
                        public void onFailure(Throwable e) {
                            LOGGER.error("Article save failed, id:{}, url:{}", id, article.getUrl(), e);
                            onError.accept(e);
                        }
                    });
        } catch (IOException e) {
            LOGGER.error("IO error while saving article with url:{}indexAndWait", article.getUrl(), e);
            onError.accept(StoreActionFailedException.createFailed(id));
        } catch (ElasticsearchException e) {
            LOGGER.error("Elasticsearch error while saving article with url:{}", article.getUrl(), e);
            onError.accept(StoreActionFailedException.createFailed(id));
        } catch (StoreActionFailedException e) {
            LOGGER.error("Internal error while saving article with url:{}", article.getUrl(), e);
            onError.accept(StoreActionFailedException.createFailed(id));
        }
    }

    @Override
    public IdResponse update(Article object) {
        return null;
    }

    @Override
    public IdResponse delete(String id) {
        DeleteResponse deleteResponse = client.prepareDelete(MAIN.indexName(), ARTICLE.typeName(), id)
                .execute()
                .actionGet();
        if (!deleteResponse.isFound()) {
            LOGGER.error("Could not find article with id:{}", id);
        }
        return IdResponse.create(id);
    }

    @Override
    public void asyncDelete(String id, Consumer<IdResponse> onSuccess, Consumer<Throwable> onError) {
        client.prepareDelete(MAIN.indexName(), ARTICLE.typeName(), id)
                .execute()
                .addListener(new ActionListener<DeleteResponse>() {
                    @Override
                    public void onResponse(DeleteResponse deleteResponse) {
                        if (deleteResponse.isFound()) {
                            onSuccess.accept(IdResponse.create(deleteResponse.getId()));
                        } else {
                            onError.accept(new ArticleNotFoundException(deleteResponse.getId()));
                        }
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        onError.accept(e);
                    }
                });
    }

    @Override
    public Optional<Article> findById(String id) throws IOException, StoreActionFailedException {
        try {
            GetResponse response = client.prepareGet(MAIN.indexName(), ARTICLE.typeName(), id)
                    .execute()
                    .actionGet();
            if (response.isExists()) {
                return Optional.of(convertToArticle(id, response.getSourceAsMap(), Action.GET));
            } else {
                return Optional.absent();
            }
        } catch (ElasticsearchException e) {
            LOGGER.error("Error in getting article with id:{}", id, e);
            throw StoreActionFailedException.getFailed(id);
        }
    }

    @Override
    public Optional<Article> findByMd5(String md5) throws IOException, StoreActionFailedException {
        try {
            SearchResponse response = client.prepareSearch(MAIN.indexName()).setTypes(ARTICLE.typeName())
                    .setSearchType(SearchType.QUERY_AND_FETCH)
                    .setQuery(QueryBuilders.matchAllQuery())
                    .setPostFilter(FilterBuilders.termFilter(ArticleFields.MD5, md5))
                    .setSize(1)
                    .setQueryCache(true)
                    .execute().actionGet();

            if (response.getHits().getTotalHits() > 0) {
                SearchHit hit = response.getHits().getAt(0);
                return Optional.of(convertToArticle(hit.getId(), hit.getSource(), Action.GET));
            } else {
                return Optional.absent();
            }
        } catch (ElasticsearchException e) {
            if (!(e instanceof IndexMissingException)) {
                LOGGER.error("Error in getting article with md5:{}", md5, e);
            }
            return Optional.absent();
        }
    }

    private Article convertToArticle(String id, Map<String, Object> map, Action action) throws StoreActionFailedException {
        Article article = new Article();
        try {
            BeanUtils.populate(article, map);
            article.setId(id);
            return article;
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOGGER.error("Error in populating code article from response map, id:{}", id, e);
            throw new StoreActionFailedException(action, id);
        }
    }

    @Override
    public PagedResponse<ArticleResult> list(PagedRequest request) throws IOException, StoreActionFailedException {
        QueryBuilder queryBuilder = queryBuilderFactory.create(request);
        SearchResponse searchResponse = client.prepareSearch(MAIN.indexName()).setTypes(ARTICLE.typeName())
                .setSearchType(SearchType.QUERY_AND_FETCH)
                .setQuery(queryBuilder)
                .setFrom(request.getStartIndex())
                .setSize(request.getPageSize())
                .execute().actionGet();
        if (searchResponse.isTerminatedEarly() != null && searchResponse.isTerminatedEarly()) {
            throw StoreActionFailedException.searchFailed(request);
        } else if (searchResponse.isTimedOut()) {
            throw StoreActionFailedException.searchTimeout(request);
        } else {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Search took:{}", searchResponse.getTook().format());
            }
            return createPageResponse(searchResponse);
        }
    }

    private PagedResponse<ArticleResult> createPageResponse(SearchResponse searchResponse) {
        List<ArticleResult> results = StreamSupport
                .stream(searchResponse.getHits().spliterator(), false)
                .map(hit -> {
                    Article article = null;
                    try {
                        article = convertToArticle(hit.getId(), hit.getSource(), Action.GET);
                    } catch (StoreActionFailedException e) {
                        throw Throwables.propagate(e);
                    }
                    return new ArticleResult(hit.getScore(), article);
                }).collect(Collectors.toList());

        return new PagedResponse<>(results, searchResponse.getHits().getTotalHits());
    }


    @Override
    public void asyncList(PagedRequest request, Consumer<PagedResponse<ArticleResult>> onResponse, Consumer<Throwable> onFailure) {
        QueryBuilder queryBuilder = queryBuilderFactory.create(request);
        client.prepareSearch(MAIN.indexName()).setTypes(ARTICLE.typeName())
                .setSearchType(SearchType.QUERY_AND_FETCH)
                .setQuery(queryBuilder)
                .addSort(queryBuilderFactory.sortBySaveDate())
                .setFrom(request.getStartIndex())
                .setSize(request.getPageSize())
                .execute().addListener(new ActionListener<SearchResponse>() {
            @Override
            public void onResponse(SearchResponse searchResponse) {
                onResponse.accept(createPageResponse(searchResponse));
            }

            @Override
            public void onFailure(Throwable e) {//NOSONAR
                handleError(e, onResponse, onFailure);
            }
        });
    }

    @Override
    public void asyncGet(GetByKeyRequest request, Consumer<PagedResponse<ArticleResult>> onResponse, Consumer<Throwable> onFailure) {
        FilterBuilder keyFilters = queryBuilderFactory.create(request);
        handleAsyncGet(onResponse, onFailure, keyFilters, Math.min(request.getCount(), 100), true);
    }

    @Override
    public void asyncGet(GetByUrlRequest request, Consumer<PagedResponse<ArticleResult>> onResponse, Consumer<Throwable> onFailure) {
        FilterBuilder urlFilters = queryBuilderFactory.create(request);
        handleAsyncGet(onResponse, onFailure, urlFilters, Math.min(request.getUrls().size(), 100), false);
    }

    protected void handleAsyncGet(final Consumer<PagedResponse<ArticleResult>> onResponse,
                                  final Consumer<Throwable> onFailure, FilterBuilder filterBuilder, int count, boolean sortByDate) {
        SearchRequestBuilder builder = client.prepareSearch(MAIN.indexName()).setTypes(ARTICLE.typeName())
                .setSearchType(SearchType.DEFAULT)
                .setQuery(QueryBuilders.matchAllQuery())
                .setPostFilter(filterBuilder)
                .setFrom(0)
                .setSize(count);
        if (sortByDate) {
            builder.addSort(queryBuilderFactory.sortBySaveDate());
        }
        builder.execute()
                .addListener(new ActionListener<SearchResponse>() {
                    @Override
                    public void onResponse(SearchResponse searchResponse) {
                        onResponse.accept(createPageResponse(searchResponse));
                    }

                    @Override
                    public void onFailure(Throwable e) { //NOSONAR
                        handleError(e, onResponse, onFailure);
                    }
                });
    }

    @Override
    public void asyncSuggestion(PagedRequest request, Consumer<SuggestionResponse> onResponse, Consumer<Throwable> onFailure) {
        if (!request.isValid()) {
            onFailure.accept(new InvalidRequestException("Request is not valid"));
            return;
        }
        articleSuggester.suggest(client, request, onResponse, onFailure);
    }


    private void handleError(Throwable e, Consumer<PagedResponse<ArticleResult>> onResponse, Consumer<Throwable> onFailure) {
        if (e instanceof IndexMissingException) {
            //return empty error
            onResponse.accept(new PagedResponse<>());
        } else {
            onFailure.accept(e);
        }

    }
}
