package es.suggestion1;

import com.pl.store.es.ArticleTitlePhraseSuggester;
import org.junit.Test;

/**
 * @author mamad
 * @since 30/11/14.
 */
public class ArticleTitlePhraseSuggesterIntegTest extends BaseSuggestionIntegTest {

    @Test
    public void testPhraseSuggestion() throws Exception {
        indexDocs();

        ArticleTitlePhraseSuggester phraseSuggester = new ArticleTitlePhraseSuggester();

        callAndVerify("washington", phraseSuggester);
    }
}
