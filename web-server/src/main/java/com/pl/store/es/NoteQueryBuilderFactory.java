package com.pl.store.es;

import com.google.common.base.Strings;
import com.pl.dsl.GetByKeyRequest;
import com.pl.dsl.PagedRequest;
import com.pl.dsl.note.NoteFields;
import com.pl.dsl.extension.GetByUrlRequest;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

import java.util.Set;
import java.util.stream.Collectors;

import static com.pl.dsl.note.NoteFields.URL;
import static com.pl.store.es.IndexName.Type.NOTE;
import static org.elasticsearch.index.query.FilterBuilders.orFilter;
import static org.elasticsearch.index.query.FilterBuilders.queryFilter;
import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * @author mamad
 * @since 15/11/14.
 */
public class NoteQueryBuilderFactory implements EsQueryBuilderFactory {
    private static final String[] NOTE_FIELDS = {"title"};

    @Override
    public QueryBuilder create(PagedRequest request) {
        if (Strings.isNullOrEmpty(request.getQuery())) {
            return matchAllQuery();
        } else {
            return multiMatchQuery(request.getQuery(), NOTE_FIELDS);
        }
    }

    @Override
    public FilterBuilder create(GetByUrlRequest request) {
        Set<FilterBuilder> filters = request.getUrls()
                .stream()
                .map(url -> queryFilter(matchPhraseQuery(URL, url)))
                .collect(Collectors.toSet());

        return orFilter(filters.toArray(new FilterBuilder[filters.size()]));
    }

    @Override
    public FilterBuilder create(GetByKeyRequest request) {
        switch (request.getKeyType()) {
            case ID:
                return FilterBuilders.idsFilter(NOTE.typeName()).ids(request.getKey());
            case LABEL:
                return FilterBuilders.termFilter(NoteFields.LABEL, request.getKey());
            default:
                throw new IllegalArgumentException("Unhandled key type:" + request.getKeyType());
        }
    }

    @Override
    public SortBuilder sortBySaveDate() {
        return SortBuilders.fieldSort(NoteFields.SAVE_DATE).order(SortOrder.DESC).missing("_last");
    }
}
