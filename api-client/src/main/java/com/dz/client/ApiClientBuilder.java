package com.dz.client;

import com.dz.api.CliApi;
import com.dz.api.ExtensionApi;
import com.dz.api.FrontendApi;
import com.dz.api.NotesApi;
import com.dz.dsl.GsonHelper;
import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import retrofit.client.Client;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;


/**
 * @author mamad
 * @since 10/11/14.
 */
public class ApiClientBuilder {
    private String serverUrl = "http://localhost:5050";
    private Client client;
    private Gson gson = GsonHelper.create().buildDefault();

    public ApiClientBuilder() {
        withOkClient();
    }

    public static ApiClientBuilder create() {
        return new ApiClientBuilder();
    }

    public ApiClientBuilder withClient(Client client) {
        this.client = client;
        return this;
    }

    public ApiClientBuilder withOkClient() {
        return withClient(new OkClient(new OkHttpClient()));
    }

    public ApiClientBuilder withGson(Gson gson) {
        this.gson = gson;
        return this;
    }

    public ApiClientBuilder withUrl(String serverUrl) {
        this.serverUrl = serverUrl;
        return this;
    }

    public FrontendApi forntendApi() {
        return ClientBuilder.create(FrontendApi.class)
                .withEndpoint(serverUrl)
                .withConverter(new GsonConverter(gson, Charsets.UTF_8.name()))
                .withClient(client)
                .build();
    }

    public ExtensionApi extensionApi() {
        return ClientBuilder.create(ExtensionApi.class)
                .withEndpoint(serverUrl)
                .withConverter(new GsonConverter(gson, Charsets.UTF_8.name()))
                .withClient(client)
                .build();
    }

    public CliApi cmdLineApi() {
        return ClientBuilder.create(CliApi.class)
                .withEndpoint(serverUrl)
                .withConverter(new GsonConverter(gson, Charsets.UTF_8.name()))
                .withClient(client)
                .build();
    }

    public NotesApi notesApi() {
        return ClientBuilder.create(NotesApi.class)
                .withEndpoint(serverUrl)
                .withConverter(new GsonConverter(gson, Charsets.UTF_8.name()))
                .withClient(client)
                .build();
    }

}
