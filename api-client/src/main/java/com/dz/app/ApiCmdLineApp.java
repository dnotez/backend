package com.dz.app;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.dz.api.FrontendApi;
import com.dz.client.ApiClientBuilder;
import com.google.gson.Gson;

/**
 * @author mamad
 * @since 10/11/14.
 */
public class ApiCmdLineApp {
    public static void main(String[] args) {
        Args arguments = new Args();
        JCommander parser = new JCommander(arguments);
        try {
            parser.parse(args);
            if (arguments.help) {
                parser.usage();
                return;
            }
        } catch (Exception e) {
            parser.usage();
            return;
        }

        ApiCmdLineApp app = new ApiCmdLineApp();
        app.run(arguments);
        System.exit(0);
    }

    private void run(Args arguments) {
        FrontendApi api = ApiClientBuilder.create().withUrl(arguments.apiUrl).forntendApi();
        Gson gson = new Gson();
        System.out.println(gson.toJson(api.getCategories()));
    }

    private static class Args {
        @Parameter(names = {"-h", "--help"}, description = "Display arguments usage help message.", help = true)
        public boolean help;

        @Parameter(names = {"-u", "--api-url"}, description = "API server url")
        public String apiUrl = "http://localhost:5050";

    }
}
