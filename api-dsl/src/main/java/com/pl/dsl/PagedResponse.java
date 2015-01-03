package com.pl.dsl;

import java.util.Collections;
import java.util.List;

/**
 * @author mamad
 * @since 13/11/14.
 */
public class PagedResponse<R> extends GeneralResponse<PagedResponse<R>> {

    private List<R> results = Collections.emptyList();
    private long total;

    public PagedResponse() {
    }

    public PagedResponse(String errorMessage) {
        super(errorMessage);
    }

    public PagedResponse(List<R> results, long total) {
        this.results = results;
        this.total = total;
    }

    public long getTotal() {
        return total;
    }

    public List<R> getResults() {
        return results;
    }

    @Override
    public PagedResponse<R> error(String message) {
        return new PagedResponse<>(message);
    }
}
