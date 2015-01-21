package com.pl.store.es;

import com.google.common.util.concurrent.Uninterruptibles;
import com.pl.EsTestHelper;
import com.pl.dsl.PagedRequest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;

/**
 * @author mamad
 * @since 29/11/14.
 */
public class NoteEsStoreSuggestionTest {
    private static EsTestHelper esHelper;

    @BeforeClass
    public static void setUp() throws Exception {
        esHelper = EsTestHelper.create()
                //.useRemoteNode()
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

    @AfterClass
    public static void tearDown() throws Exception {
        if (esHelper != null) {
            esHelper.stopEsNode();
            esHelper = null;
        }
    }

    @Test
    public void testPhraseSuggestion() throws Exception {
        NoteEsStore store = esHelper.getStore();

        PagedRequest request = PagedRequest.suggestion("te");
        AtomicBoolean called = new AtomicBoolean(false);
        AtomicBoolean hasSuggestion = new AtomicBoolean(false);

        store.asyncSuggestion(request, response -> {
            assertNotNull(response);
            assertFalse(response.isError());
            assertNotNull(response.getResults());
            assertFalse(response.getResults().isEmpty());
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
}
