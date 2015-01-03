package com.pl.store.es;

import com.pl.dsl.PagedRequest;
import com.pl.dsl.article.ArticleFields;
import com.pl.dsl.article.SuggestionResponse;
import com.pl.dsl.article.SuggestionResult;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.suggest.SuggestResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.indices.IndexMissingException;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.pl.store.es.IndexName.MAIN;

/**
 * A suggester based on <a href="http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/search-suggesters-completion.html">
 * Elasticsearch completion suggester</a>
 *
 * @author mamad
 * @see com.pl.store.es.ArticleTitlePhraseSuggester
 * @since 30/11/14.
 */
public class ArticleTitleCompletionSuggester implements ArticleSuggester {
    public static final Logger LOGGER = LoggerFactory.getLogger(ArticleTitleCompletionSuggester.class);

    @Override
    public void suggest(Client client, PagedRequest request, Consumer<SuggestionResponse> onResponse, Consumer<Throwable> onFailure) {
        CompletionSuggestionBuilder suggestionBuilder = SuggestBuilders
                .completionSuggestion(ArticleFields.TITLE)
                .text(request.getQuery())
                .size(request.getPageSize())
                .field(ArticleFields.SUGGEST);
        client.prepareSuggest(MAIN.indexName())
                .addSuggestion(suggestionBuilder)
                .execute().addListener(new ActionListener<SuggestResponse>() {
            @Override
            public void onResponse(SuggestResponse suggestResponse) {
                onResponse.accept(createSuggestionResponse(suggestResponse));
            }

            @Override
            public void onFailure(Throwable e) {
                if (!(e instanceof IndexMissingException)) {
                    LOGGER.error("Error in getting completion suggestion.", e);
                    onFailure.accept(e);
                } else {
                    LOGGER.warn("Index missing");
                    onResponse.accept(new SuggestionResponse());
                }
            }
        });
    }

    private SuggestionResponse createSuggestionResponse(SuggestResponse suggestResponse) {
        Suggest suggest = suggestResponse.getSuggest();
        if (suggest.size() < 1) {
            return new SuggestionResponse("No suggestion");
        }
        CompletionSuggestion suggestion = (CompletionSuggestion) suggest.iterator().next();
        Iterable<SuggestionResult> results = suggestion.getEntries().get(0).getOptions().stream()
                .map(this::toSuggestionResult)
                .collect(Collectors.toList());
        return new SuggestionResponse().withResults(results);
    }

    private SuggestionResult toSuggestionResult(CompletionSuggestion.Entry.Option option) {
        //payload it the id of the article which will be used for redirecting to original article
        String id = option.getPayloadAsMap().get(ArticleCompletionFields.ID).toString();
        return new SuggestionResult(option.getText().string(), id);
    }
}
