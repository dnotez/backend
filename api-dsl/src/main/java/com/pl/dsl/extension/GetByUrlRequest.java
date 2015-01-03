package com.pl.dsl.extension;

import com.google.common.collect.Sets;
import com.pl.dsl.article.ArticleFields;

import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Arrays.asList;

/**
 * Request for getting items, based on the url.
 *
 * @author mamad
 * @since 23/11/14.
 */
public class GetByUrlRequest {
    private List<String> urls;
    // fields of the requested object to be included in the response
    private Set<String> fields = Sets.newHashSet();

    public GetByUrlRequest() {
    }

    public GetByUrlRequest(List<String> urls) {
        this.urls = urls;
    }

    public static GetByUrlRequest of(String... urls) {
        return new GetByUrlRequest(asList(checkNotNull(urls)));
    }

    public GetByUrlRequest withFields(String... fields) {
        this.fields.addAll(asList(checkNotNull(fields)));
        return this;
    }

    public GetByUrlRequest idOnly() {
        return withFields(ArticleFields.ID);
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }
}
