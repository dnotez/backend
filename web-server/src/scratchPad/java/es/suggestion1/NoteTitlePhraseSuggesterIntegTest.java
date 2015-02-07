package es.suggestion1;

import com.dz.store.es.NoteTitlePhraseSuggester;
import org.junit.Test;

/**
 * @author mamad
 * @since 30/11/14.
 */
public class NoteTitlePhraseSuggesterIntegTest extends BaseSuggestionIntegTest {

    @Test
    public void testPhraseSuggestion() throws Exception {
        indexDocs();

        NoteTitlePhraseSuggester phraseSuggester = new NoteTitlePhraseSuggester();

        callAndVerify("washington", phraseSuggester);
    }
}
