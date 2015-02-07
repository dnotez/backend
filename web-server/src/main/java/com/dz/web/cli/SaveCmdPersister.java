package com.dz.web.cli;

import com.dz.dsl.cli.SaveCmdRequest;
import com.dz.dsl.cli.SaveCmdResponse;

import java.util.function.Consumer;

/**
 * @author mamad
 * @since 13/12/14.
 */
public interface SaveCmdPersister {
    void persist(SaveCmdRequest request, Consumer<SaveCmdResponse> onSuccess, Consumer<Throwable> onError);
}
