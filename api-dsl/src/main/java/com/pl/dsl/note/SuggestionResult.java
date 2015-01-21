package com.pl.dsl.note;

import com.google.common.base.MoreObjects;

/**
 * A specific result type used in suggestion, contains only highlighted title phrase
 *
 * @author mamad
 * @since 28/11/14.
 */
public class SuggestionResult {
    /**
     * Highlighted suggestion
     */
    private String suggestion;

    /**
     * Note id, if user select the suggestion and hit the enter, this id will be used to generated a redirect to the note url.
     * Note: the redirect (http code 301) is required for analytics and personal usage ranking.
     */
    private String id;


    public SuggestionResult(String suggestion, String id) {
        this.suggestion = suggestion;
        this.id = id;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("suggestion", suggestion)
                .add("id", id)
                .toString();
    }
}
