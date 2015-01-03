package com.pl.store.es;

import com.google.inject.Singleton;
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
import org.elasticsearch.search.suggest.phrase.PhraseSuggestion;
import org.elasticsearch.search.suggest.phrase.PhraseSuggestionBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.pl.store.es.IndexName.MAIN;

/**
 * A suggester based on <a href="http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/search-suggesters-phrase.html">
 * Elasticsearch phrase suggester</a>
 *
 * @author mamad
 * @see com.pl.store.es.ArticleTitleCompletionSuggester
 * @since 30/11/14.
 */

@Singleton
public class ArticleTitlePhraseSuggester implements ArticleSuggester {
    public static final Logger LOGGER = LoggerFactory.getLogger(ArticleTitlePhraseSuggester.class);

    @Override
    public void suggest(Client client, PagedRequest request, Consumer<SuggestionResponse> onResponse, Consumer<Throwable> onFailure) {
        PhraseSuggestionBuilder.DirectCandidateGenerator candidateGenerator = PhraseSuggestionBuilder
                .candidateGenerator(ArticleFields.SUGGEST_PHRASE)
                .size(request.getPageSize())
                .suggestMode("always");

        PhraseSuggestionBuilder suggestionBuilder = SuggestBuilders
                .phraseSuggestion(ArticleFields.TITLE)
                .field(ArticleFields.SUGGEST_PHRASE)
                .text(request.getQuery())
                        //convert these to parameters to customize them
                .confidence(0F)
                .maxErrors(2F)
                .shardSize(30000)
                .size(request.getPageSize())
                .addCandidateGenerator(candidateGenerator)
                .highlight("<em>", "</em>");
        client.prepareSuggest(MAIN.indexName())
                .addSuggestion(suggestionBuilder)
                .execute()
                .addListener(new ActionListener<SuggestResponse>() {
                    @Override
                    public void onResponse(SuggestResponse suggestResponse) {
                        onResponse.accept(createSuggestionResponse(suggestResponse));
                    }

                    @Override
                    public void onFailure(Throwable e) { // NOSONAR
                        if (!(e instanceof IndexMissingException)) {
                            LOGGER.error("Error in getting phrase suggestion.", e);
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
        PhraseSuggestion suggestion = (PhraseSuggestion) suggest.iterator().next();
        Iterable<SuggestionResult> results = suggestion.getEntries().get(0).getOptions().stream()
                .map(this::toSuggestionResult)
                .collect(Collectors.toList());
        return new SuggestionResponse().withResults(results);
    }

    private SuggestionResult toSuggestionResult(PhraseSuggestion.Entry.Option option) {
        return new SuggestionResult(option.getHighlighted().string(), "no-url");
    }
}
