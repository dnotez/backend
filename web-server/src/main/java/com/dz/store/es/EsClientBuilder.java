package com.dz.store.es;

import com.google.common.io.Files;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.node.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

/**
 * @author mamad
 * @since 13/11/14.
 */
public final class EsClientBuilder {
    public static final Logger LOGGER = LoggerFactory.getLogger(EsClientBuilder.class);

    public static Node localNode(String cluster) {
        return localNode(cluster, Files.createTempDir());
    }

    public static Node localNode(String cluster, File dataFolder) {
        ImmutableSettings.Builder settings = ImmutableSettings.settingsBuilder()
                .put("http.enabled", "false")
                .put("path.data", dataFolder.getPath());
        return nodeBuilder().clusterName(cluster).local(true).settings(settings).node();
    }

    public static Client localClient(String cluster) {
        return localNode(cluster).client();
    }

    public static Client client(String cluster) {
        LOGGER.debug("Building ES client for cluster:{}", cluster);
        return nodeBuilder().clusterName(cluster).client(true).node().client();
    }

}
