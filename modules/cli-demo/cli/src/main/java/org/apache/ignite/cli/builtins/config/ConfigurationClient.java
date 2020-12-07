/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.ignite.cli.builtins.config;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigRenderOptions;
import org.apache.ignite.cli.IgniteCLIException;

public class ConfigurationClient {

    private final String GET_URL = "/management/v1/configuration";
    private final String SET_URL = "/management/v1/configuration";

    private final HttpClient httpClient;
    private final ObjectMapper mapper = new ObjectMapper();

    public ConfigurationClient() {
        httpClient = HttpClient
            .newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .build();
    }

    public String get(String host, int port) {
        var request = HttpRequest
            .newBuilder()
            .GET()
            .header("Content-type", "application/json")
            .uri(URI.create("http://" + host + ":" + port + GET_URL))
            .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return mapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(mapper.readValue(response.body(), JsonNode.class));
        }
        catch (IOException | InterruptedException e) {
            throw new IgniteCLIException("Connection issues while trying to send http request");
        }
    }

    public String set(String host, int port, String rawHoconData) {
        var config = ConfigFactory.parseString(rawHoconData);
        var jsonConfig = config.root().render(ConfigRenderOptions.concise());
        var request = HttpRequest
            .newBuilder()
            .POST(HttpRequest.BodyPublishers.ofString(jsonConfig))
            .header("Content-Type", "application/json")
            .uri(URI.create("http://" + host + ":" + port + SET_URL))
            .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == HttpURLConnection.HTTP_OK)
                return "";
            else
                return "Http error code: " + response.statusCode() + "\n" +
                    "Error message: " + response.body();
        }
        catch (IOException | InterruptedException e) {
            throw new IgniteCLIException("Connection issues while trying to send http request");
        }
    }
}
