package es.suggestion1;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.Uninterruptibles;
import com.pl.dsl.PagedRequest;
import com.pl.dsl.note.SuggestionResponse;
import com.pl.store.es.NoteCompletionSuggesterComposer;
import com.pl.store.es.NoteSuggester;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.test.ElasticsearchIntegrationTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.pl.dsl.note.NoteFields.SUGGEST;
import static com.pl.dsl.note.NoteFields.SUGGEST_PHRASE;
import static com.pl.store.es.IndexName.MAIN;
import static com.pl.store.es.IndexName.Type.NOTE;
import static org.elasticsearch.cluster.metadata.IndexMetaData.SETTING_NUMBER_OF_SHARDS;
import static org.elasticsearch.common.settings.ImmutableSettings.settingsBuilder;
import static org.elasticsearch.test.hamcrest.ElasticsearchAssertions.assertAcked;

/**
 * @author mamad
 * @since 30/11/14.
 */
public class BaseSuggestionIntegTest extends ElasticsearchIntegrationTest {
    protected void indexDocs() throws IOException, InterruptedException, ExecutionException {
        CreateIndexRequestBuilder builder = prepareCreate(MAIN.indexName()).setSettings(settingsBuilder()
                .put(indexSettings())
                        // A single shard will help to keep the tests repeatable.
                .put(SETTING_NUMBER_OF_SHARDS, 1)
                .put("index.analysis.analyzer.text.tokenizer", "standard")
                .putArray("index.analysis.analyzer.text.filter", "lowercase", "my_shingle")
                .put("index.analysis.filter.my_shingle.type", "shingle")
                .put("index.analysis.filter.my_shingle.output_unigrams", true)
                .put("index.analysis.filter.my_shingle.min_shingle_size", 2)
                .put("index.analysis.filter.my_shingle.max_shingle_size", 3));

        XContentBuilder mapping = XContentFactory.jsonBuilder()
                .startObject()
                .startObject(NOTE.typeName())
                .startObject("properties")
                .startObject(SUGGEST_PHRASE)
                .field("type", "string")
                .field("analyzer", "text")
                .endObject()
                .startObject(SUGGEST)
                .field("type", "completion")
                .field("analyzer", "stopword")
                .field("payloads", true)
                .endObject()
                .endObject()
                .endObject()
                .endObject();
        assertAcked(builder.addMapping(NOTE.typeName(), mapping));
        ensureGreen();

        ImmutableList.Builder<String> titles = ImmutableList.<String>builder();

        titles.add("United States House of Representatives Elections in Washington 2006");
        titles.add("United States House of Representatives Elections in Washington 2005");
        titles.add("State");
        titles.add("Houses of Parliament");
        titles.add("Representative Government");
        titles.add("Election");

        List<IndexRequestBuilder> builders = new ArrayList<>();
        NoteCompletionSuggesterComposer suggesterComposer = new NoteCompletionSuggesterComposer();
        for (String title : titles.build()) {
            Map<String, Object> suggestionObject = suggesterComposer.createSuggestionObject(title, title);
            XContentBuilder note = XContentFactory.jsonBuilder()
                    .startObject()
                    .field(SUGGEST_PHRASE, title)
                    .field(SUGGEST, suggestionObject)
                    .endObject();
            builders.add(client().prepareIndex(MAIN.indexName(), NOTE.typeName())
                    .setSource(note));
        }
        indexRandom(true, builders);
    }

    protected void callAndVerify(String query, NoteSuggester suggester) {
        PagedRequest request = PagedRequest.suggestion(query);
        AtomicBoolean called = new AtomicBoolean(false);
        AtomicBoolean hasSuggestion = new AtomicBoolean(false);

        suggester.suggest(client(), request, response -> {
            assertNotNull(response);
            assertFalse(response.isError());
            assertNotNull(response.getResults());
            assertFalse(response.getResults().isEmpty());
            inspectResponse(response);
            called.set(true);
            hasSuggestion.set(true);
        }, e -> {
            called.set(true);
            e.printStackTrace();
            fail();
        });

        while (!called.get()) {
            Uninterruptibles.sleepUninterruptibly(100, TimeUnit.MILLISECONDS);
        }
        assertTrue(called.get());
        assertTrue(hasSuggestion.get());

    }

    protected void inspectResponse(SuggestionResponse response) {

    }
}
