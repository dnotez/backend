package com.pl.store.es;

import com.pl.dsl.GetByKeyRequest;
import com.pl.dsl.PagedRequest;
import com.pl.dsl.extension.GetByUrlRequest;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.sort.SortBuilder;

/**
 * @author mamad
 * @since 15/11/14.
 */
public interface EsQueryBuilderFactory {
    QueryBuilder create(PagedRequest request);

    FilterBuilder create(GetByUrlRequest request);

    FilterBuilder create(GetByKeyRequest request);

    SortBuilder sortBySaveDate();
}
