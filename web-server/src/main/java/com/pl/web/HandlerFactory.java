package com.pl.web;

import com.google.common.collect.ImmutableList;
import com.pl.web.api.ApiHandlers;
import com.pl.web.api.bookmark.BookmarkModule;
import com.pl.web.api.category.CategoryModule;
import com.pl.web.cli.CliApiHandler;
import com.pl.web.cli.CliModule;
import com.pl.web.extension.ExtensionApiHandler;
import com.pl.web.user.UsersChainHandler;
import ratpack.codahale.metrics.CodaHaleMetricsModule;
import ratpack.codahale.metrics.MetricsWebsocketBroadcastHandler;
import ratpack.guice.Guice;
import ratpack.handling.Handler;
import ratpack.handling.Handlers;
import ratpack.launch.LaunchConfig;

/**
 * @author mamad
 * @since 01/11/14.
 */
public class HandlerFactory implements ratpack.launch.HandlerFactory {

    private final String clusterName;
    private final boolean localJvm;
    private final int metricUpdateInterval;
    private final String url;

    public HandlerFactory(String clusterName, boolean localJvm, int metricUpdateInterval, String url) {
        this.clusterName = clusterName;
        this.localJvm = localJvm;
        this.metricUpdateInterval = metricUpdateInterval;
        this.url = url;
    }

    @Override
    public Handler create(LaunchConfig launchConfig) throws Exception {
        return Guice.builder(launchConfig)
                .bindings(bindings -> {
                    bindings.add(new CommonsModule());
                    bindings.add(new TextExtractionModule());
                    bindings.add(new ContentDownloaderModule());
                    bindings.add(new EsStoreModule(clusterName, localJvm));
                    bindings.add(new CategoryModule());
                    bindings.add(new CliModule(url));
                    bindings.add(new BookmarkModule());
                    if (metricUpdateInterval > 0) {
                        bindings.add(new CodaHaleMetricsModule().jvmMetrics().jmx().websocket().healthChecks());
                    }
                })
                .build(chain -> chain
                        .prefix("cli", chain.getRegistry().get(CliApiHandler.class))
                        .prefix("users", chain.getRegistry().get(UsersChainHandler.class))
                        .prefix("api", chain.getRegistry().get(ApiHandlers.class))
                        .prefix("extension", chain.getRegistry().get(ExtensionApiHandler.class))
                                //.prefix("", nested -> nested.assets("."))
                                //.prefix("app/index.html", nested -> nested.assets("index.html"))
                        .handler("admin/system/metrics", chain.getRegistry().get(MetricsWebsocketBroadcastHandler.class))
                        .handler(Handlers.assets(launchConfig, ".", ImmutableList.of("/", "index.html")))
                        .handler(context -> context.render(String.format("root handler, url:'%s' is not handled.",
                                context.getRequest().getPath()))));
    }
}