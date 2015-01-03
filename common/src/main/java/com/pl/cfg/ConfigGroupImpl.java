package com.pl.cfg;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;

import javax.validation.constraints.NotNull;

/**
 * @author Mamad Asgari
 * @since 22/09/2014
 */
public class ConfigGroupImpl implements ConfigGroup {
    final String name;
    final Configuration configuration;

    protected ConfigGroupImpl(String name, CompositeConfiguration configuration) {
        this.name = name.toLowerCase();
        this.configuration = configuration.subset(name);
    }

    public void defaultValue(String key, Object value) {
        if (configuration.getProperty(key) == null) {
            configuration.setProperty(key, value);
        }
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Param entry(String key) {
        if (Strings.isNullOrEmpty(key) || key.toLowerCase().startsWith(name)) {
            throw new IllegalArgumentException("Invalid name:${key}");
        }
        return new ParamWrapper(configuration, key);
    }

    void onBeforeValueUpdated(@NotNull String key, @NotNull Optional<Param> optional) {
        //todo: notify param listeners
    }
}
