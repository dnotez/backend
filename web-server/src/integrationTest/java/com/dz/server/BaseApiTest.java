package com.dz.server;

import com.dz.EsTestHelper;
import com.dz.store.es.IndexName;
import com.dz.store.es.NoteTitleCompletionSuggester;
import com.google.common.io.Files;
import com.google.common.util.concurrent.Uninterruptibles;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthStatus;
import org.elasticsearch.client.Requests;
import org.elasticsearch.index.query.QueryBuilders;
import org.hamcrest.Matchers;
import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.Assert.assertThat;

/**
 * @author mamad
 * @since 01/12/14.
 */
public class BaseApiTest {
    protected static EsTestHelper esHelper;
    private static BackendServer server;
    private static String CLUSTER_NAME = UUID.randomUUID().toString();

    private static String url;

    private static void startServer() {
        server = BackendServer.builder()
                .cluster(CLUSTER_NAME)
                .localNode(true)
                .port(5050)
                .url("http://localhost:" + 5050)
                .base(Files.createTempDir().getPath())
                .build();

        server.start();
    }

    private static String ensureServerIsUp() {
        Optional<String> optional = server.getEndpoint();
        Assume.assumeTrue("Server must be up and running", optional.isPresent());
        return optional.get();
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        esHelper = EsTestHelper.create()
                //.useRemoteNode()
                .withCluster(CLUSTER_NAME)
                .startEsNode()
                .prepareEs()
                .ensureGreen()
                .withSuggester(new NoteTitleCompletionSuggester())
                .buildNoteStore()
                .wipeIndex()
                .indexSampleData()
                .ensureGreen()
                .ensureIndexed();

        startServer();
        url = ensureServerIsUp();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        esHelper.stopEsNode();
        server.stop();
        server = null;
        esHelper = null;
    }

    @Before
    public void setUp() throws Exception {
        prepareApi(url);
    }

    protected void prepareApi(String url) {
    }

    protected void ensureSaved(IndexName index, long expectedCount) {
        ensureYellow();
        int tried = 0;
        long count = 0;
        while (tried < 5) {
            count = esHelper.client().prepareCount(index.indexName()).setQuery(QueryBuilders.matchAllQuery())
                    .execute()
                    .actionGet().getCount();
            tried++;
            if (count >= expectedCount) {
                break;
            }
            Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
        }
        assertThat(count, greaterThanOrEqualTo(expectedCount));
    }

    protected void ensureYellow() {
        ClusterHealthRequest request = Requests.clusterHealthRequest();
        ClusterHealthResponse response = esHelper.client().admin().cluster()
                .health(request)
                .actionGet(30, TimeUnit.SECONDS);
        if (response.isTimedOut()) {
            throw new RuntimeException("Timeout in checking ES cluster health");
        }
        assertThat(response.getStatus(), Matchers.not(ClusterHealthStatus.RED));
    }
}
