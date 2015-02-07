package com.dz.store.es;

import com.dz.dsl.GetByKeyRequest;
import com.dz.dsl.PagedRequest;
import com.dz.dsl.extension.GetByUrlRequest;
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
