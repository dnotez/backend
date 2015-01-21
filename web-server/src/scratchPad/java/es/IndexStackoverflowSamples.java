package es;

import com.pl.EsTestHelper;
import com.pl.store.es.NoteTitleCompletionSuggester;

/**
 * @author mamad
 * @since 04/12/14.
 */
public class IndexStackoverflowSamples {
    public static void main(String[] args) {
        EsTestHelper.create()
                .useRemoteNode()
                .withCluster("push_lists")
                .startEsNode()
                .prepareEs()
                .ensureGreen()
                .withSuggester(new NoteTitleCompletionSuggester())
                .buildNoteStore()
                .indexSampleData()
                .ensureGreen()
                .ensureIndexed();
    }
}
