package es;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pl.store.es.EsClientBuilder;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.pl.store.es.IndexName.MAIN;
import static com.pl.store.es.IndexName.Type.ARTICLE;
import static org.junit.Assert.*;

/**
 * @author mamad
 * @since 15/11/14.
 */
public class TestGetAll {
    @Test
    public void testGetAll() throws Exception {
        Client client = EsClientBuilder.client("push_lists");
        SearchResponse searchResponse = client.prepareSearch(MAIN.indexName()).setTypes(ARTICLE.typeName())
                .setSearchType(SearchType.QUERY_AND_FETCH)
                .setQuery(QueryBuilders.matchAllQuery())
                .setFrom(0)
                .setSize(10)
                .execute().actionGet();
        assertNotNull(searchResponse);
        assertNotNull(searchResponse.getHits());
        assertTrue(searchResponse.getHits().getTotalHits() > 0);
        assertTrue(searchResponse.getHits().getHits().length > 0);
        client.close();
    }

    @Test
    public void testAsyncGet() throws Exception {
        Client client = EsClientBuilder.client("push_lists");
        final AtomicBoolean called = new AtomicBoolean();
        client.prepareSearch(MAIN.indexName()).setTypes(ARTICLE.typeName())
                .setSearchType(SearchType.QUERY_AND_FETCH)
                .setQuery(QueryBuilders.matchAllQuery())
                .setFrom(0)
                .setSize(10)
                .execute().addListener(new ActionListener<SearchResponse>() {
            @Override
            public void onResponse(SearchResponse searchResponse) {
                assertNotNull(searchResponse);
                assertNotNull(searchResponse.getHits());
                assertTrue(searchResponse.getHits().getTotalHits() > 0);
                assertTrue(searchResponse.getHits().getHits().length > 0);
                called.set(true);
            }

            @Override
            public void onFailure(Throwable e) {
                fail(e.getMessage());
                called.set(true);
            }
        });
        while (!called.get()) {
            Thread.sleep(300);
        }
    }

    @Test
    public void testAsyncJacksonJsonGeneration() throws Exception {
        Client client = EsClientBuilder.client("push_lists");
        final AtomicBoolean called = new AtomicBoolean();
        client.prepareSearch(MAIN.indexName()).setTypes(ARTICLE.typeName())
                .setSearchType(SearchType.QUERY_AND_FETCH)
                .setQuery(QueryBuilders.matchAllQuery())
                .setFrom(0)
                .setSize(10)
                .execute().addListener(new ActionListener<SearchResponse>() {
            @Override
            public void onResponse(SearchResponse searchResponse) {
                assertNotNull(searchResponse);
                assertNotNull(searchResponse.getHits());
                assertTrue(searchResponse.getHits().getTotalHits() > 0);
                assertTrue(searchResponse.getHits().getHits().length > 0);
                ObjectMapper mapper = new ObjectMapper();
                StringWriter sw = new StringWriter();
                try {
                    JsonGenerator generator = mapper.getFactory().createGenerator(sw);
                    generator.writeStartObject();
                    generator.writeNumberField("total", searchResponse.getHits().getTotalHits());
                    generator.writeArrayFieldStart("results");
                    generator.writeEndArray();
                    generator.writeEndObject();
                    generator.close();
                    System.out.println(sw.toString());
                } catch (IOException e) {
                    fail(e.getMessage());
                }
                called.set(true);
            }

            @Override
            public void onFailure(Throwable e) {
                fail(e.getMessage());
                called.set(true);
            }
        });
        while (!called.get()) {
            Thread.sleep(300);
        }
    }
}
