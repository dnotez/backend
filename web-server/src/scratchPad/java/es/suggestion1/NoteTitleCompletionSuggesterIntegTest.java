package es.suggestion1;


import com.dz.dsl.note.SuggestionResponse;
import com.dz.store.es.IndexName;
import com.dz.store.es.NoteTitleCompletionSuggester;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.Test;

/**
 * @author mamad
 * @since 30/11/14.
 */
public class NoteTitleCompletionSuggesterIntegTest extends BaseSuggestionIntegTest {
    @Test
    public void testWithPositionIncrement() throws Exception {
        indexDocs();
        SearchResponse response = client().prepareSearch(IndexName.MAIN.indexName())
                .setQuery(QueryBuilders.matchAllQuery())
                .execute().actionGet();
        assertEquals(6, response.getHits().getTotalHits());
        NoteTitleCompletionSuggester completionSuggester = new NoteTitleCompletionSuggester();
        callAndVerify("of", completionSuggester);
    }

    @Override
    protected void inspectResponse(SuggestionResponse response) {
        super.inspectResponse(response);
    }
}
