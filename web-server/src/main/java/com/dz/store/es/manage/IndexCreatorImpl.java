package com.dz.store.es.manage;

import com.dz.store.es.IndexName;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequestBuilder;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsRequest;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsRequestBuilder;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mamad
 * @since 14/11/14.
 */
public class IndexCreatorImpl implements IndexCreator {
    public static final Logger LOGGER = LoggerFactory.getLogger(IndexCreatorImpl.class);
    private final Client client;

    public IndexCreatorImpl(Client client) {
        this.client = client;
    }

    @Override
    public boolean createMappings() {
        IndicesAdminClient indicesAdmin = client.admin().indices();
        GetMappingsRequestBuilder getBuilder = new GetMappingsRequestBuilder(indicesAdmin);
        for (IndexName indexName : IndexName.values()) {
            getBuilder.setIndices(indexName.name().toLowerCase());
            for (IndexName.Type type : indexName.getTypes()) {
                GetMappingsRequest getRequest = getBuilder.setTypes(type.name()).request();
                indicesAdmin.getMappings(getRequest, new ActionListener<GetMappingsResponse>() {
                    @Override
                    public void onResponse(GetMappingsResponse getMappingsResponse) {
                        //mapping exist, do nothing
                    }

                    @Override
                    public void onFailure(Throwable e) {

                    }
                });
            }
        }

        //indicesAdmin.preparePutMapping(IndexName.MAIN.name().toLowerCase()).setSource("")
        return false;
    }

    @Override
    public boolean createIndices() {
        IndicesAdminClient indicesAdmin = client.admin().indices();
        IndicesExistsRequest existRequest = new IndicesExistsRequestBuilder(indicesAdmin)
                .setIndices()
                .request();
        indicesAdmin.exists(existRequest, new ActionListener<IndicesExistsResponse>() {
            @Override
            public void onResponse(IndicesExistsResponse indicesExistsResponse) {

            }

            @Override
            public void onFailure(Throwable e) {

            }
        });

        return false;
    }

}
