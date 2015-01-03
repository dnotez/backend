package es;

import com.google.common.collect.ImmutableList;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.search.*;
import org.elasticsearch.action.suggest.SuggestRequestBuilder;
import org.elasticsearch.action.suggest.SuggestResponse;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.phrase.PhraseSuggestionBuilder;
import org.elasticsearch.test.ElasticsearchIntegrationTest;
import org.junit.Test;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

import static org.elasticsearch.cluster.metadata.IndexMetaData.SETTING_NUMBER_OF_SHARDS;
import static org.elasticsearch.common.settings.ImmutableSettings.settingsBuilder;
import static org.elasticsearch.search.suggest.SuggestBuilders.phraseSuggestion;
import static org.elasticsearch.test.hamcrest.ElasticsearchAssertions.*;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Copied from Elasticsearch source code: SuggestSearchTests
 * To run this test, in the module dependencies, load randomizedtesting-runner and lucene-test-framework before lucene-core
 *
 * @author mamad
 * @since 30/11/14.
 */
public class EsPhraseSuggester extends ElasticsearchIntegrationTest {

    @Test
    public void testPhraseSuggesterCollate() throws InterruptedException, ExecutionException, IOException {
        CreateIndexRequestBuilder builder = prepareCreate("test").setSettings(settingsBuilder()
                .put(indexSettings())
                .put(SETTING_NUMBER_OF_SHARDS, 1) // A single shard will help to keep the tests repeatable.
                .put("index.analysis.analyzer.text.tokenizer", "standard")
                .putArray("index.analysis.analyzer.text.filter", "lowercase", "my_shingle")
                .put("index.analysis.filter.my_shingle.type", "shingle")
                .put("index.analysis.filter.my_shingle.output_unigrams", true)
                .put("index.analysis.filter.my_shingle.min_shingle_size", 2)
                .put("index.analysis.filter.my_shingle.max_shingle_size", 3));

        XContentBuilder mapping = XContentFactory.jsonBuilder()
                .startObject()
                .startObject("type1")
                .startObject("properties")
                .startObject("title")
                .field("type", "string")
                .field("analyzer", "text")
                .endObject()
                .endObject()
                .endObject()
                .endObject();
        assertAcked(builder.addMapping("type1", mapping));
        ensureGreen();

        ImmutableList.Builder<String> titles = ImmutableList.<String>builder();

        titles.add("United States House of Representatives Elections in Washington 2006");
        titles.add("United States House of Representatives Elections in Washington 2005");
        titles.add("State");
        titles.add("Houses of Parliament");
        titles.add("Representative Government");
        titles.add("Election");

        List<IndexRequestBuilder> builders = new ArrayList<>();
        for (String title : titles.build()) {
            builders.add(client().prepareIndex("test", "type1").setSource("title", title));
        }
        indexRandom(true, builders);

        // suggest without filtering
        PhraseSuggestionBuilder suggest = phraseSuggestion("title")
                .field("title")
                .addCandidateGenerator(PhraseSuggestionBuilder.candidateGenerator("title")
                                .suggestMode("always")
                                .maxTermFreq(.99f)
                                .size(10)
                                .maxInspections(200)
                )
                .confidence(0f)
                .maxErrors(2f)
                .shardSize(30000)
                .size(10);
        Suggest searchSuggest;
        //searchSuggest = searchSuggest("washington", suggest);
        searchSuggest = searchSuggest("united states house of representatives elections in washington 2006", suggest);
        assertSuggestionSize(searchSuggest, 0, 10, "title");

        // suggest with filtering
        String filterString = XContentFactory.jsonBuilder()
                .startObject()
                .startObject("match_phrase")
                .field("title", "{{suggestion}}")
                .endObject()
                .endObject()
                .string();
        PhraseSuggestionBuilder filteredQuerySuggest = suggest.collateQuery(filterString);
        searchSuggest = searchSuggest("united states house of representatives elections in washington 2006", filteredQuerySuggest);
        assertSuggestionSize(searchSuggest, 0, 2, "title");

        // filtered suggest with no result (boundary case)
        searchSuggest = searchSuggest("Elections of Representatives Parliament", filteredQuerySuggest);
        assertSuggestionSize(searchSuggest, 0, 0, "title");

        NumShards numShards = getNumShards("test");

        // filtered suggest with bad query
        String incorrectFilterString = XContentFactory.jsonBuilder()
                .startObject()
                .startObject("test")
                .field("title", "{{suggestion}}")
                .endObject()
                .endObject()
                .string();
        PhraseSuggestionBuilder incorrectFilteredSuggest = suggest.collateQuery(incorrectFilterString);
        try {
            searchSuggest("united states house of representatives elections in washington 2006", numShards.numPrimaries, incorrectFilteredSuggest);
            fail("Post query error has been swallowed");
        } catch (ElasticsearchException e) {
            // expected
        }

        // suggest with filter collation
        String filterStringAsFilter = XContentFactory.jsonBuilder()
                .startObject()
                .startObject("query")
                .startObject("match_phrase")
                .field("title", "{{suggestion}}")
                .endObject()
                .endObject()
                .endObject()
                .string();

        PhraseSuggestionBuilder filteredFilterSuggest = suggest.collateQuery(null).collateFilter(filterStringAsFilter);
        searchSuggest = searchSuggest("united states house of representatives elections in washington 2006", filteredFilterSuggest);
        assertSuggestionSize(searchSuggest, 0, 2, "title");

        // filtered suggest with bad filter
        String filterStr = XContentFactory.jsonBuilder()
                .startObject()
                .startObject("pprefix")
                .field("title", "{{suggestion}}")
                .endObject()
                .endObject()
                .string();

        PhraseSuggestionBuilder in = suggest.collateQuery(null).collateFilter(filterStr);
        try {
            searchSuggest("united states house of representatives elections in washington 2006", numShards.numPrimaries, in);
            fail("Post filter error has been swallowed");
        } catch (ElasticsearchException e) {
            //expected
        }

        // collate script failure due to no additional params
        String collateWithParams = XContentFactory.jsonBuilder()
                .startObject()
                .startObject("{{query_type}}")
                .field("{{query_field}}", "{{suggestion}}")
                .endObject()
                .endObject()
                .string();


        PhraseSuggestionBuilder phraseSuggestWithNoParams = suggest.collateFilter(null).collateQuery(collateWithParams);
        try {
            searchSuggest("united states house of representatives elections in washington 2006", numShards.numPrimaries, phraseSuggestWithNoParams);
            fail("Malformed query (lack of additional params) should fail");
        } catch (ElasticsearchException e) {
            // expected
        }

        // collate script with additional params
        Map<String, Object> params = new HashMap<>();
        params.put("query_type", "match_phrase");
        params.put("query_field", "title");

        PhraseSuggestionBuilder phraseSuggestWithParams = suggest.collateFilter(null).collateQuery(collateWithParams).collateParams(params);
        searchSuggest = searchSuggest("united states house of representatives elections in washington 2006", phraseSuggestWithParams);
        assertSuggestionSize(searchSuggest, 0, 2, "title");

        //collate request defining both query/filter should fail
        PhraseSuggestionBuilder phraseSuggestWithFilterAndQuery = suggest.collateFilter(filterStringAsFilter).collateQuery(filterString);
        try {
            searchSuggest("united states house of representatives elections in washington 2006", numShards.numPrimaries, phraseSuggestWithFilterAndQuery);
            fail("expected parse failure, as both filter and query are set in collate");
        } catch (ElasticsearchException e) {
            // expected
        }

        // collate request with prune set to true
        PhraseSuggestionBuilder phraseSuggestWithParamsAndReturn = suggest.collateFilter(null).collateQuery(collateWithParams).collateParams(params).collatePrune(true);
        searchSuggest = searchSuggest("united states house of representatives elections in washington 2006", phraseSuggestWithParamsAndReturn);
        assertSuggestionSize(searchSuggest, 0, 10, "title");
        assertSuggestionPhraseCollateMatchExists(searchSuggest, "title", 2);

    }


    protected Suggest searchSuggest(SuggestBuilder.SuggestionBuilder<?>... suggestion) {
        return searchSuggest(null, suggestion);
    }

    protected Suggest searchSuggest(String suggestText, SuggestBuilder.SuggestionBuilder<?>... suggestions) {
        return searchSuggest(suggestText, 0, suggestions);
    }

    protected Suggest searchSuggest(String suggestText, int expectShardsFailed, SuggestBuilder.SuggestionBuilder<?>... suggestions) {
        if (randomBoolean()) {
            SearchRequestBuilder builder = client().prepareSearch().setSearchType(SearchType.COUNT);
            if (suggestText != null) {
                builder.setSuggestText(suggestText);
            }
            for (SuggestBuilder.SuggestionBuilder<?> suggestion : suggestions) {
                builder.addSuggestion(suggestion);
            }
            SearchResponse actionGet = builder.execute().actionGet();
            assertThat(Arrays.toString(actionGet.getShardFailures()), actionGet.getFailedShards(), equalTo(expectShardsFailed));
            return actionGet.getSuggest();
        } else {
            SuggestRequestBuilder builder = client().prepareSuggest();
            if (suggestText != null) {
                builder.setSuggestText(suggestText);
            }
            for (SuggestBuilder.SuggestionBuilder<?> suggestion : suggestions) {
                builder.addSuggestion(suggestion);
            }

            SuggestResponse actionGet = builder.execute().actionGet();
            assertThat(Arrays.toString(actionGet.getShardFailures()), actionGet.getFailedShards(), equalTo(expectShardsFailed));
            if (expectShardsFailed > 0) {
                throw new SearchPhaseExecutionException("suggest", "Suggest execution failed", new ShardSearchFailure[0]);
            }
            return actionGet.getSuggest();
        }
    }
}
