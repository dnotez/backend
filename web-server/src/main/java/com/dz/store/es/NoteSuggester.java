package com.dz.store.es;

import com.dz.dsl.PagedRequest;
import com.dz.dsl.note.SuggestionResponse;
import org.elasticsearch.client.Client;

import java.util.function.Consumer;

/**
 * @author mamad
 * @since 30/11/14.
 */
public interface NoteSuggester {
    void suggest(Client client, PagedRequest request, Consumer<SuggestionResponse> onResponse, Consumer<Throwable> onFailure);
}
