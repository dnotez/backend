package com.dz.cfg;

import com.google.common.net.HostAndPort;

import java.util.Date;

/**
 * Configuration and single levels like:
 * <group>.<key>=<value>
 * <p>
 * Try using this instead of org.apache.commons.configuration.Configuration
 *
 * @author Mamad Asgari
 * @since 22/09/2014
 */
public interface Param {

    String asString();

    HostAndPort asHostAndPort();

    Date asDate();

    int asInt();

    long asLong();

    boolean asBool();

    Iterable<Integer> asIntIterable();

    Iterable<String> asStrIterable();

    int size();

    Param withDefault(String defaultValue);

    Param withDefault(int defaultValue);

    Param withDefault(long defaultValue);

    Param withDefault(boolean defaultValue);

    Param withDefault(int... defaultValue);

    Param withDefault(String... defaultValue);
}