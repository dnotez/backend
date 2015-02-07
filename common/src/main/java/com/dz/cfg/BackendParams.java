package com.dz.cfg;

import com.google.common.base.Preconditions;

/**
 * @author Mamad Asgari
 * @since 22/09/2014
 */
public final class BackendParams implements ConfigGroup {

    public static final String NAME = "dz";

    public static final String PORT = "port";
    public static final String BASE_DIR = "base";

    /**
     * Key for URL address of the server, example value for this key can be http://www.dz.com
     */
    public static final String SERVER_URL = "url";

    private final ConfigGroupImpl delegate;

    protected BackendParams(ConfigGroupImpl delegate) {
        this.delegate = delegate;
        Preconditions.checkArgument(NAME.equals(delegate.name()));
        //register defaults
        int port = 5050;
        delegate.defaultValue(PORT, port);
        delegate.defaultValue(BASE_DIR, "assets");
        delegate.defaultValue(SERVER_URL, "http://localhost:" + port);
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public Param entry(String key) {
        return delegate.entry(key);
    }

    public Param port() {
        return entry(PORT);
    }

    public Param baseDir() {
        return entry(BASE_DIR);
    }

    public Param url() {
        return entry(SERVER_URL);
    }
}
