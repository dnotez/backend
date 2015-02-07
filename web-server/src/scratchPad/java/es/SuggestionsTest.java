package es;

import com.dz.store.es.EsClientBuilder;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequestBuilder;
import org.elasticsearch.action.admin.indices.template.get.GetIndexTemplatesRequestBuilder;
import org.elasticsearch.action.admin.indices.template.get.GetIndexTemplatesResponse;
import org.elasticsearch.action.admin.indices.template.put.PutIndexTemplateRequestBuilder;
import org.elasticsearch.action.admin.indices.template.put.PutIndexTemplateResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.suggest.SuggestResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.UUID;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Test mappings for different suggestions
 *
 * @author mamad
 * @since 21/11/14.
 */
public class SuggestionsTest {
    public static final String INDEX = "main";
    public static final String TYPE = "note";
    public static final String TEMPLATE = "template0";
    private Client client;

    @Before
    public void setUp() throws Exception {
        //client = EsClientBuilder.client("push_list");
        client = EsClientBuilder.localClient(UUID.randomUUID().toString());
        IndicesAdminClient indices = client.admin().indices();
        GetIndexTemplatesResponse templatesResponse = new GetIndexTemplatesRequestBuilder(indices, TEMPLATE)
                .execute()
                .actionGet();
        if (templatesResponse.getIndexTemplates().isEmpty()) {
            //put index template
            File templateFile = new File("backend/src/main/resources/main_template.json");
            Assume.assumeTrue(templateFile.getPath(), templateFile.exists() && templateFile.isFile());
            String mapping = Files.toString(templateFile, Charsets.UTF_8);
            PutIndexTemplateResponse templateResponse = new PutIndexTemplateRequestBuilder(indices, TEMPLATE)
                    .setSource(mapping)
                    .execute()
                    .actionGet();
            Assume.assumeNotNull(templateResponse);
            Assume.assumeTrue(templateResponse.isAcknowledged());
        }

        if (!new IndicesExistsRequestBuilder(indices).setIndices(INDEX).execute().actionGet().isExists()) {

            //now index some documents
            BulkRequestBuilder bulk = client.prepareBulk();
            for (int i = 0; i < 100; i++) {
                XContentBuilder object = jsonBuilder().startObject()
                        .field("id", Integer.toString(i))
                        .field("title", "title" + i)
                        .field("title_bigram", "title" + i)
                        .field("url", "url" + i)
                        .field("body", "body" + i)
                                //suggestion
                        .field("suggest")
                        .startObject()
                        .field("input", "title" + i)
                        .field("output", "title - " + i)
                        .field("payload")
                                //payload
                        .startObject()
                        .field("index", i)
                        .endObject()
                                //end suggest
                        .endObject()
                                //end note
                        .endObject();
                bulk.add(client.prepareIndex(INDEX, TYPE, Integer.toString(i)).setSource(object));
            }
            BulkResponse bulkResponse = bulk.execute().actionGet();
            Assume.assumeFalse(bulkResponse.hasFailures());
        }

        //wait a little
        int wait = 0;
        while (wait < 5 && client.prepareCount(INDEX).execute().actionGet().getCount() < 100) {
            Thread.sleep(1000);
            wait++;
        }
        Assume.assumeFalse(wait >= 5);
    }

    @Test
    public void testCompletion() throws Exception {
        CompletionSuggestionBuilder completionBuilder = SuggestBuilders.completionSuggestion("s1").field("suggest").text("t");
        SuggestResponse response = client.prepareSuggest(INDEX).addSuggestion(completionBuilder).execute().actionGet();
        assertNotNull(response);
        assertNotNull(response.getSuggest());
        assertTrue(response.getSuggest().size() > 0);
    }

    @Test
    public void testPrefixShingle() throws Exception {


    }
}
