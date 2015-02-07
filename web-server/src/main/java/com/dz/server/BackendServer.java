package com.dz.server;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.dz.cfg.Configs;
import com.dz.web.HandlerFactory;
import com.google.common.base.Throwables;
import com.google.common.net.HostAndPort;
import ratpack.launch.LaunchConfig;
import ratpack.launch.LaunchConfigBuilder;
import ratpack.server.RatpackServer;
import ratpack.server.RatpackServerBuilder;

import java.io.File;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.net.HostAndPort.fromParts;

/**
 * @author mamad
 * @since 10/11/14.
 */
public class BackendServer {
    private final RatpackServer ratpackServer;

    public BackendServer(String clusterName, boolean localJvm, int port, String base, int metricUpdateInterval, String url) {
        HandlerFactory handlerFactory = new HandlerFactory(clusterName, localJvm, metricUpdateInterval, url);
        File baseDir = new File(base);
        if (!baseDir.exists() || !baseDir.isDirectory()) {
            throw new RuntimeException("Invalid base dir:" + base);
        }
        LaunchConfig launchConfig = LaunchConfigBuilder
                .baseDir(baseDir)
                .port(port)
                        // 20 MB
                .maxContentLength(20 * 1048576)
                .other("metrics.scheduledreporter.interval", Integer.toString(metricUpdateInterval))
                .build(handlerFactory);
        this.ratpackServer = RatpackServerBuilder.build(launchConfig);
    }

    public static void main(String[] arguments) {
        Args args = new Args();
        JCommander parser = new JCommander(args);
        try {
            parser.parse(arguments);
            if (args.isHelp()) {
                parser.usage();
                return;
            }
        } catch (Exception e) {
            parser.usage();
            return;
        }

        BackendServer server = BackendServer.builder()
                .cluster(args.getClusterName())
                .port(args.getPort())
                .base(args.getBase())
                .url(args.getServerUrl())
                .metricsUpdateInterval(args.getMetricUpdateInterval())
                .build();

        server.start();
    }

    public static Builder builder() {
        return new Builder();
    }

    public void start() {
        try {
            ratpackServer.start();
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    public void stop() {
        try {
            ratpackServer.stop();
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    public Optional<HostAndPort> getBinding() {
        if (ratpackServer.isRunning()) {
            return Optional.of(fromParts(ratpackServer.getBindHost(), ratpackServer.getBindPort()));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Url of server, like http://localhost:5050
     *
     * @return server url
     */
    public Optional<String> getEndpoint() {
        Optional<HostAndPort> optional = getBinding();
        if (optional.isPresent()) {
            return Optional.of(ratpackServer.getScheme() + "://" + optional.get().toString());
        } else {
            return Optional.empty();
        }
    }

    private static class Args {
        @Parameter(names = {"-h", "--help"}, description = "Display arguments usage help message.", help = true)
        private boolean help;

        @Parameter(names = {"-p", "--port"}, description = "Backend server listen port.")
        private int port = Configs.backend().port().asInt();

        @Parameter(names = {"-m", "--metrics"}, description = "Interval for updating metrics (in seconds). use 0 to disable metrics")
        private int metricUpdateInterval = 0;

        @Parameter(names = {"-b", "--base-dir"}, description = "Base directory for serving static resources, mapped to '/' path.")
        private String base = Configs.backend().baseDir().asString();

        @Parameter(names = {"-c", "--cluster"}, description = "Name of ES cluster.")
        private String clusterName = Configs.es().clusterName().asString();

        @Parameter(names = {"-u", "--url"}, description = "Server url for creating links")
        private String serverUrl = Configs.backend().url().asString();

        public boolean isHelp() {
            return help;
        }

        public int getPort() {
            return port;
        }

        public String getBase() {
            return base;
        }

        public String getClusterName() {
            return clusterName;
        }

        public String getServerUrl() {
            return serverUrl;
        }

        public int getMetricUpdateInterval() {
            return metricUpdateInterval;
        }
    }

    public static final class Builder {
        private String clusterName = Configs.es().clusterName().asString();
        private boolean localJvm = false;
        private int port = Configs.backend().port().asInt();
        private String base = Configs.backend().baseDir().asString();
        private int metricUpdateInterval = 30;
        private String url;

        public Builder cluster(String clusterName) {
            this.clusterName = clusterName;
            return this;
        }

        public Builder localNode(boolean localJvm) {
            this.localJvm = localJvm;
            return this;
        }

        public Builder port(int port) {
            this.port = port;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }
        public Builder base(String base) {
            this.base = base;
            return this;
        }

        public Builder metricsUpdateInterval(int metricUpdateInterval) {
            this.metricUpdateInterval = metricUpdateInterval;
            return this;
        }

        public BackendServer build() {
            checkNotNull(url, "url can not be null.");
            checkNotNull(clusterName, "clusterName can not be null.");
            checkNotNull(base, "base can not be null.");
            return new BackendServer(clusterName, localJvm, port, base, metricUpdateInterval, url);
        }
    }
}
