package org.affinity.rdating;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.util.Credentials;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Test {

    public static void main(String[] args) throws Exception {
        String user = args[0];
        String password = args[1];
        String clientId = args[2];
        String secret = args[3];
        String credentials = Base64.getEncoder().encodeToString(String.format("%s:%s", clientId, secret).getBytes(StandardCharsets.UTF_8));

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://www.reddit.com/api/v1/access_token"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("User-Agent", "rdating/0.1 (by /u/Food-Little)")
                .header("Authorization", String.format("Basic %s", credentials))
                .POST(HttpRequest.BodyPublishers.ofString(String.format("grant_type=password&username=%s&password=%s", user, password)))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String responseBody = response.body();
        // Parse JSON response to extract access_token
        String accessToken = null;
        try {
            JsonNode jsonNode =
                new ObjectMapper().readTree(responseBody);
            accessToken = jsonNode.get("access_token").asText();
        } catch (Exception e) {
            System.err.println("Failed to parse access_token: " + e.getMessage());
        }
        System.out.println("accessToken = " + accessToken);

        //after is this value: jq '.data.children[0].data.name'
//        HttpRequest userRequest = HttpRequest.newBuilder()
//                .uri(URI.create("https://oauth.reddit.com/r/fishcommunity12345/new.json?limit=1000"))
//                .header("User-Agent", "rdating/0.1 (by /u/Food-Little)")
//                .header("Authorization", "Bearer " + accessToken)
//                .build();
//
//        response = client.send(userRequest, HttpResponse.BodyHandlers.ofString());
//        System.out.println(response.body());

        //1m4afk7


        HttpRequest userRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://oauth.reddit.com/r/fishcommunity12345/comments/1m4afk7.json?limit=1"))
                .header("User-Agent", "rdating/0.1 (by /u/Food-Little)")
                .header("Authorization", "Bearer " + accessToken)
                .build();

        response = client.send(userRequest, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());



//        HttpRequest userRequest = HttpRequest.newBuilder()
//                .uri(URI.create("https://oauth.reddit.com/api/compose"))
//                .header("Content-Type", "application/x-www-form-urlencoded")
//                .header("User-Agent", "rdating/0.1 (by /u/Food-Little)")
//                .header("Authorization", "Bearer " + accessToken)
//                .POST(HttpRequest.BodyPublishers.ofString(
//                        "api_type=json" +
//                        "&from_sr=fishcommunity12345" +
//                        "&subject=Test%20Message" +
//                        "&text=You matched!" +
//                        "&to=Food-Little"))
//                .build();
//
//        response = client.send(userRequest, HttpResponse.BodyHandlers.ofString());
//        System.out.println(response.body());
    }

}
