package com.pl;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteStreams;
import com.google.common.util.concurrent.Uninterruptibles;
import com.pl.duplicate.MD5DuplicateStreamDetector;
import com.pl.extraction.TikaPlainTextExtractor;
import com.pl.store.es.*;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthStatus;
import org.elasticsearch.action.admin.indices.template.put.PutIndexTemplateResponse;
import org.elasticsearch.action.count.CountResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.Priority;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.indices.IndexMissingException;
import org.hamcrest.Matchers;
import org.junit.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.pl.store.es.IndexName.MAIN;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.Assume.*;

/**
 * Helper class to initialise Elasticsearch for integration tests
 *
 * @author mamad
 * @since 29/11/14.
 */
public class EsTestHelper {

    private String clusterName = UUID.randomUUID().toString();
    private Client client;

    private BulkIndexer indexer = EsTestDataIndexer.create();
    private ArticleEsStore store;
    private ArticleSuggester suggester = new ArticleTitlePhraseSuggester();
    private boolean localJvm = true;
    private int indexedDocs;


    public static EsTestHelper create() {
        return new EsTestHelper();
    }

    public EsTestHelper withIndexer(BulkIndexer indexer) {
        this.indexer = indexer;
        return this;
    }

    public EsTestHelper withCluster(String clusterName) {
        this.clusterName = clusterName;
        return this;
    }

    public EsTestHelper useRemoteNode() {
        this.localJvm = false;
        return this;
    }

    public EsTestHelper startEsNode() {
        client = localJvm ? EsClientBuilder.localClient(clusterName) : EsClientBuilder.client(clusterName);
        return this;
    }

    public EsTestHelper stopEsNode() {
        client.close();
        return this;
    }

    public EsTestHelper prepareEs() {
        try (InputStream mappingJsonStream = this.getClass().getResourceAsStream("/main_template.json")) {
            assumeNotNull("resource 'main_template.json' must be available.", mappingJsonStream);
            PutIndexTemplateResponse response = client.admin().indices()
                    .preparePutTemplate("article_template")
                    .setSource(ByteStreams.toByteArray(mappingJsonStream))
                    .execute()
                    .actionGet();
            assumeTrue("template mapping must be stored successfully.", response.isAcknowledged());
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail("IO Error in reading mapping json");
        }
        /*
        //now create index
        CreateIndexResponse createIndexResponse = client.admin().indices()
                .create(Requests.createIndexRequest(MAIN.indexName()))
                .actionGet();
        assumeTrue(createIndexResponse.isAcknowledged());
        */
        return this;
    }

    public Client client() {
        return client;
    }

    public ArticleEsStore getStore() {
        return store;
    }

    public EsTestHelper indexSampleData() {
        this.indexedDocs = indexer.index(store);

        return this;
    }

    public EsTestHelper withSuggester(ArticleSuggester suggester) {
        this.suggester = suggester;
        return this;
    }

    public EsTestHelper buildArticleStore() {
        Preconditions.checkNotNull(client);
        SimpleUUIDGenerator uuidGenerator = new SimpleUUIDGenerator();
        DefaultTimeouts timeouts = new DefaultTimeouts();
        ArticleQueryBuilderFactory queryBuilderFactory = new ArticleQueryBuilderFactory();
        MD5DuplicateStreamDetector duplicateDetector = new MD5DuplicateStreamDetector();
        TikaPlainTextExtractor textExtractor = new TikaPlainTextExtractor();
        ArticleSuggestionComposer completionSuggestionComposer = new ArticleCompletionSuggesterComposer();
        IndexableArticleComposer articleComposer = new IndexableArticleComposerImpl(textExtractor, completionSuggestionComposer);

        store = new ArticleEsStore(client, uuidGenerator, timeouts, queryBuilderFactory, duplicateDetector, suggester, articleComposer);
        return this;
    }

    public EsTestHelper ensureGreen() {
        ClusterHealthRequest healthRequest = Requests
                .clusterHealthRequest()
                .timeout(TimeValue.timeValueSeconds(30))
                .waitForGreenStatus()
                .waitForEvents(Priority.HIGH)
                .waitForRelocatingShards(1);
        ClusterHealthResponse actionGet = client.admin().cluster().health(healthRequest).actionGet();
        assumeFalse("Timeout should not happen in checking cluster state", actionGet.isTimedOut());
        assumeThat("Cluster state must be green", ClusterHealthStatus.GREEN, equalTo(actionGet.getStatus()));
        return this;
    }

    public EsTestHelper ensureIndexed() {
        return ensureIndexed(indexedDocs);
    }

    public EsTestHelper ensureIndexed(long numDocs) {
        long indexed = 0;
        int wait = 0;
        while (wait < 10) {
            try {
                CountResponse countResponse = client.prepareCount(MAIN.indexName())
                        .setQuery(QueryBuilders.matchAllQuery())
                        .execute().actionGet();
                indexed = countResponse.getCount();
            } catch (IndexMissingException e) {
                //ignored
            }
            if (indexed >= numDocs) {
                break;
            }
            Uninterruptibles.sleepUninterruptibly(500, TimeUnit.MILLISECONDS);
            wait++;
        }
        assumeThat("Number of indexed documents must be equal or grater than:" + numDocs, indexed, greaterThanOrEqualTo(numDocs));
        return this;
    }

    public EsTestHelper wipeIndex() {
        try {
            assumeTrue("Index must be wiped successfully.", client.admin().indices()
                    .delete(Requests.deleteIndexRequest(MAIN.indexName()))
                    .actionGet()
                    .isAcknowledged());
        } catch (ElasticsearchException e) {
            if (!(e instanceof IndexMissingException)) {
                throw e;
            }
        }
        return this;
    }

    public EsTestHelper deleteAllDocs() {
        try {
            assumeThat("Index must be wiped successfully.", client
                    .prepareDeleteByQuery(MAIN.indexName())
                    .setQuery(QueryBuilders.matchAllQuery())
                    .execute()
                    .actionGet().status().getStatus(), Matchers.equalTo(200));
        } catch (ElasticsearchException e) {
            if (!(e instanceof IndexMissingException)) {
                throw e;
            }
        }

        return this;
    }
}
