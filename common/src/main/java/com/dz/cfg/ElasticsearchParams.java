package com.dz.cfg;

import com.google.common.base.Preconditions;

import javax.validation.constraints.NotNull;

/**
 * @author Mamad Asgari
 * @since 27/09/2014
 */
public final class ElasticsearchParams implements ConfigGroup {
    public static final String DEFAULT_CLUSTER_NAME = "dnotez";

    public static final String NAME = "es";
    public static final String CLUSTER_NAME = "cluster.name";

    private final ConfigGroupImpl delegate;

    public ElasticsearchParams(ConfigGroupImpl delegate) {
        this.delegate = delegate;
        Preconditions.checkArgument(NAME.equals(delegate.name()));
        delegate.defaultValue(CLUSTER_NAME, DEFAULT_CLUSTER_NAME);
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public Param entry(@NotNull String key) {
        return delegate.entry(key);
    }

    public Param clusterName() {
        return entry(CLUSTER_NAME);
    }
}
