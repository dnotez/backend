package com.pl.web;

import com.google.inject.AbstractModule;
import com.pl.store.es.*;
import org.elasticsearch.client.Client;

/**
 * Configure Elasticsearch store bindings
 *
 * @author mamad
 * @since 13/11/14.
 */
public class EsStoreModule extends AbstractModule {
    //name of the Elasticsearch client
    private final String clusterName;

    //if true, a local JVM node will be created, use it for testing
    private final boolean localJvm;

    public EsStoreModule(String clusterName, boolean localJvm) {
        this.clusterName = clusterName;
        this.localJvm = localJvm;
    }

    @Override
    protected void configure() {
        Client client = localJvm ? EsClientBuilder.localClient(clusterName) : EsClientBuilder.client(clusterName);
        bind(Client.class).toInstance(client);
        bind(EsQueryBuilderFactory.class).to(NoteQueryBuilderFactory.class);
        bind(UUIDGenerator.class).to(SimpleUUIDGenerator.class);
        bind(ActionTimeouts.class).to(DefaultTimeouts.class);
        bind(NoteSuggestionComposer.class).to(NoteCompletionSuggesterComposer.class);
        bind(IndexableNoteComposer.class).to(IndexableNoteComposerImpl.class);
        bind(NoteSuggester.class).to(NoteTitleCompletionSuggester.class);
        bind(NoteStore.class).to(NoteEsStore.class);
    }
}
