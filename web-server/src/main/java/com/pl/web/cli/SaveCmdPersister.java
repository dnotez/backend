package com.pl.web.cli;

import com.pl.dsl.cli.SaveCmdRequest;
import com.pl.dsl.cli.SaveCmdResponse;

import java.util.function.Consumer;

/**
 * @author mamad
 * @since 13/12/14.
 */
public interface SaveCmdPersister {
    void persist(SaveCmdRequest request, Consumer<SaveCmdResponse> onSuccess, Consumer<Throwable> onError);
}
