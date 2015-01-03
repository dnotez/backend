package com.pl.cfg;

import javax.validation.constraints.NotNull;


/**
 * @author Mamad Asgari
 * @since 22/09/2014
 */
public interface ConfigGroup {
    String name();

    Param entry(@NotNull String key);
}