package com.dz.web.api;

import com.dz.dsl.IdResponse;
import com.dz.dsl.PagedRequest;
import com.dz.dsl.ResourceResponse;
import com.dz.store.es.StoreActionFailedException;
import com.dz.web.HandlerHelper;
import com.dz.web.StatusHelper;
import com.google.common.base.Optional;
import com.google.common.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.exec.Fulfiller;
import ratpack.func.Action;
import ratpack.handling.Chain;
import ratpack.handling.Context;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;

import static com.dz.web.AsyncHelper.newErrorAction;
import static com.dz.web.AsyncHelper.newJsonAction;

/**
 * todo: use async for all methods
 *
 * @author mamad
 * @since 13/11/14.
 */
@Deprecated
public abstract class DAOHandler<R> implements Action<Chain> {
    public static final Logger LOGGER = LoggerFactory.getLogger(DAOHandler.class);
    protected final HandlerHelper handlerHelper;
    private final Class<R> typeClass;
    private final Type requestTypeToken;

    public DAOHandler(Class<R> typeClass, HandlerHelper handlerHelper) {
        this.typeClass = typeClass;
        this.handlerHelper = handlerHelper;
        this.requestTypeToken = new TypeToken<PagedRequest>() {
        }.getType();
    }

    @Override
    public void execute(Chain chain) throws Exception {
        //list
        chain.handler("list", context -> {
            PagedRequest request;
            try {
                request = handlerHelper.fromBody(context, requestTypeToken);
            } catch (Exception e) {
                LOGGER.error("Could not find parser for parsing the post body", e);
                StatusHelper.sendInternalError(context, e);
                return;
            }
            asyncList(request, context);
        });

        //get by id
        chain.get("/:id", this::handleGeyAsync);

        //save new
        chain.post(this::handleSaveAsync);

        //update
        chain.put(this::handleUpdateAsync);

        chain.delete(this::handleDeleteAsync);
    }


    void handleGeyAsync(Context context) throws Exception {
        context.promise(new Action<Fulfiller<String>>() {
            @Override
            public void execute(Fulfiller<String> fulfiller) throws Exception {
                String id = context.getPathTokens().get("id");
                Optional<R> optional;
                try {
                    optional = findById(id);
                    if (optional.isPresent()) {
                        handlerHelper.jsonConsumer(fulfiller).accept(new ResourceResponse<>(optional.get()));
                    } else {
                        StatusHelper.sendNotFound(context, id);
                    }
                } catch (IOException e) {
                    LOGGER.error("IO error during getting object with id:{}", id, e);
                    StatusHelper.sendInternalError(context, e);
                } catch (StoreActionFailedException e) {
                    LOGGER.error("Store failed, could not find object with id:{}", id, e);
                    StatusHelper.sendInternalError(context, e);
                }
            }
        }).onError(newErrorAction(context))
                .then(newJsonAction(context));
    }

    void handleSaveAsync(Context context) throws Exception {
        context.promise(new Action<Fulfiller<String>>() {
            @Override
            public void execute(Fulfiller<String> fulfiller) throws Exception {
                R object;
                try {
                    object = handlerHelper.fromBody(context, typeClass);
                } catch (Exception e) {
                    LOGGER.error("Could not find parser for parsing the post body", e);
                    StatusHelper.sendInternalError(context, e);
                    return;
                }
                try {
                    IdResponse response = save(object);
                    handlerHelper.jsonConsumer(fulfiller).accept(response);
                } catch (IOException e) {
                    LOGGER.error("IO error during saving object:{}", object, e);
                    StatusHelper.sendInternalError(context, e);
                } catch (StoreActionFailedException e) {
                    LOGGER.error("Store failed, action:{}, id:{}, object:{}", e.getAction(), e.getId(), object, e);
                    StatusHelper.sendInternalError(context, e);
                }
            }
        })
                .onError(newErrorAction(context))
                .then(newJsonAction(context, HttpURLConnection.HTTP_CREATED));
    }

    void handleUpdateAsync(Context context) throws Exception {
        context.promise(new Action<Fulfiller<String>>() {
            @Override
            public void execute(Fulfiller<String> fulfiller) throws Exception {
                R object = handlerHelper.fromBody(context, typeClass);
                IdResponse response = update(object);
                handlerHelper.jsonConsumer(fulfiller).accept(response);
            }
        })
                .onError(newErrorAction(context))
                .then(newJsonAction(context));
    }

    void handleDeleteAsync(Context context) throws Exception {
        context.promise(new Action<Fulfiller<String>>() {
            @Override
            public void execute(Fulfiller<String> fulfiller) throws Exception {
                String id = context.getPathTokens().get("id");
                IdResponse response = delete(id);
                handlerHelper.jsonConsumer(fulfiller).accept(response);
            }
        })
                .onError(newErrorAction(context))
                .then(newJsonAction(context));
    }

    public abstract void asyncList(PagedRequest request, Context context);

    protected abstract Optional<R> findById(String id) throws IOException, StoreActionFailedException;

    protected abstract IdResponse save(R object) throws IOException, StoreActionFailedException;

    protected abstract IdResponse update(R object);

    protected abstract IdResponse delete(String id);
}
