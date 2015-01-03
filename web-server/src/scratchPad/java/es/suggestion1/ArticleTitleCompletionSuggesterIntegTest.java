package es.suggestion1;


import com.pl.dsl.article.SuggestionResponse;
import com.pl.store.es.ArticleTitleCompletionSuggester;
import com.pl.store.es.IndexName;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.Test;

/**
 * @author mamad
 * @since 30/11/14.
 */
public class ArticleTitleCompletionSuggesterIntegTest extends BaseSuggestionIntegTest {
    @Test
    public void testWithPositionIncrement() throws Exception {
        indexDocs();
        SearchResponse response = client().prepareSearch(IndexName.MAIN.indexName())
                .setQuery(QueryBuilders.matchAllQuery())
                .execute().actionGet();
        assertEquals(6, response.getHits().getTotalHits());
        ArticleTitleCompletionSuggester completionSuggester = new ArticleTitleCompletionSuggester();
        callAndVerify("of", completionSuggester);
    }

    @Override
    protected void inspectResponse(SuggestionResponse response) {
        super.inspectResponse(response);
    }
}
