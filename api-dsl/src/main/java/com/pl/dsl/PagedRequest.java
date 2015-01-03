package com.pl.dsl;

import com.google.common.base.Strings;

/**
 * @author mamad
 * @since 13/11/14.
 */
public class PagedRequest {
    private int startIndex = 0, pageSize = 10;

    private String query;
    //todo: add sort, filters, etc


    public PagedRequest() {
    }

    public PagedRequest(String query) {
        this.query = query;
    }

    public static PagedRequest suggestion(String query) {
        return new PagedRequest(query);
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public boolean isValid() {
        return !Strings.isNullOrEmpty(query);
    }
}
