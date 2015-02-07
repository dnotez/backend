package com.dz.client;

import com.squareup.okhttp.OkHttpClient;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.Client;
import retrofit.client.OkClient;
import retrofit.converter.Converter;

/**
 * Retrofit based REST client
 *
 * @author Mamad Asgari
 * @since 22/09/2014
 */
public class ClientBuilder<T> {
    private final Class<T> restServiceClass;
    private RestAdapter.Builder builder = new RestAdapter.Builder();

    public ClientBuilder(Class<T> restServiceClass) {
        this.restServiceClass = restServiceClass;
    }

    public static <T> ClientBuilder<T> create(Class<T> restServiceClass) {
        return new ClientBuilder<>(restServiceClass);
    }

    public ClientBuilder<T> withConverter(Converter converter) {
        builder.setConverter(converter);
        return this;
    }

    public ClientBuilder<T> withEndpoint(String url) {
        builder.setEndpoint(url);
        return this;
    }

    public ClientBuilder<T> withClient(final Client client) {
        builder.setClient(client);
        return this;
    }

    public ClientBuilder<T> withOkHttp() {
        return withClient(new OkClient(new OkHttpClient()));
    }

    public ClientBuilder<T> withParam(String name, String value) {
        builder.setRequestInterceptor(new FixedQueryParamRequestInterceptor(name, value));
        return this;
    }

    public T build() {
        return builder.build().create(restServiceClass);
    }

    public static class FixedQueryParamRequestInterceptor implements RequestInterceptor {
        private final String paramName;
        private final String paramValue;

        public FixedQueryParamRequestInterceptor(String paramName, String paramValue) {
            this.paramValue = paramValue;
            this.paramName = paramName;
        }

        public static FixedQueryParamRequestInterceptor create(String paramName, String paramValue) {
            return new FixedQueryParamRequestInterceptor(paramName, paramValue);
        }

        @Override
        public void intercept(RequestFacade request) {
            request.addQueryParam(paramName, paramValue);
        }
    }
}
