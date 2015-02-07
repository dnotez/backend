package com.dz.cfg;

import com.google.common.collect.Iterables;
import com.google.common.net.HostAndPort;
import org.apache.commons.configuration.Configuration;

import java.util.Arrays;
import java.util.Date;

/**
 * @author Mamad Asgari
 * @since 23/09/2014
 */
public class ParamWrapper implements Param {
    final Configuration subset;
    final String key;

    ParamWrapper(Configuration subset, String key) {
        this.subset = subset;
        this.key = key;
    }

    @Override
    public String asString() {
        return subset.getString(key);
    }

    @Override
    public HostAndPort asHostAndPort() {
        return HostAndPort.fromString(asString());
    }

    @Override
    public Date asDate() {
        return new Date(asLong());
    }

    @Override
    public int asInt() {
        return subset.getInt(key);
    }

    @Override
    public long asLong() {
        return subset.getLong(key);
    }

    @Override
    public boolean asBool() {
        return subset.getBoolean(key);
    }

    @Override
    public Iterable<Integer> asIntIterable() {
        return Iterables.transform(subset.getList(key), input -> ((Integer) input));
    }

    @Override
    public Iterable<String> asStrIterable() {
        return Iterables.transform(subset.getList(key), input -> ((String) input));
    }

    @Override
    public int size() {
        return subset.getList(key).size();
    }

    @Override
    public Param withDefault(String defaultValue) {
        subset.setProperty(key, defaultValue);
        return this;
    }

    @Override
    public Param withDefault(int defaultValue) {
        subset.setProperty(key, defaultValue);
        return this;
    }

    @Override
    public Param withDefault(long defaultValue) {
        subset.setProperty(key, defaultValue);
        return this;
    }

    @Override
    public Param withDefault(boolean defaultValue) {
        subset.setProperty(key, defaultValue);
        return this;
    }

    @Override
    public Param withDefault(int... defaultValues) {
        subset.setProperty(key, Arrays.asList(defaultValues));
        return this;
    }

    @Override
    public Param withDefault(String... defaultValues) {
        subset.setProperty(key, Arrays.asList(defaultValues));
        return this;
    }
}
