package org.affinity.rdating;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.util.Credentials;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Test {

    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://www.reddit.com/api/v1/access_token"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("User-Agent", "rdating/0.1 (by /u/Food-Little)")
                .header("Authorization", "Basic x")
                .POST(HttpRequest.BodyPublishers.ofString("grant_type=password&username=x&password=x"))
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
        HttpRequest userRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://oauth.reddit.com/r/fishcommunity12345/new.json?limit=100&after=t3_1lsplv1"))
                .header("User-Agent", "rdating/0.1 (by /u/Food-Little)")
                .header("Authorization", "Bearer " + accessToken)
                .build();


        response = client.send(userRequest, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
    }

}
