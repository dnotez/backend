package com.dz.dsl.note;

import com.dz.dsl.GeneralResponse;
import com.google.common.base.MoreObjects;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author mamad
 * @since 28/11/14.
 */
public class SuggestionResponse extends GeneralResponse<SuggestionResponse> {
    private List<SuggestionResult> results = Lists.newArrayList();

    public SuggestionResponse(String errorMessage) {
        super(errorMessage);
    }

    public SuggestionResponse() {
    }

    public SuggestionResponse withResults(Iterable<SuggestionResult> results) {
        Iterables.addAll(this.results, results);
        return this;
    }

    @Override
    public SuggestionResponse error(String message) {
        return new SuggestionResponse(message);
    }

    public List<SuggestionResult> getResults() {
        return results;
    }

    public void setResults(List<SuggestionResult> results) {
        this.results = results;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("count", results.size())
                .add("results", results)
                .toString();
    }
}
