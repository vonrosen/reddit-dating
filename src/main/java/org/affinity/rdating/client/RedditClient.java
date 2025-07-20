package org.affinity.rdating.client;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@Service
public class RedditClient {
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson;

    @Value("${reddit.client.id}")
    private String clientId;

    @Value("${reddit.client.secret}")
    private String secret;

    @Value("${reddit.client.username}")
    private String userName;

    @Value("${reddit.client.password}")
    private String password;

    @Value("${reddit.authUrl}")
    private String authUrl;

    @Value("${reddit.apiBaseUrl}")
    private String apiBaseUrl;

    @Value("${reddit.client.user-agent}")
    private String userAgent;

    private AuthToken authToken;

    public RedditClient(Gson gson) {
        this.gson = gson;
    }

    public RedditPostListing getPosts(String subreddit, int limit) throws IOException, InterruptedException {
        return getPosts(subreddit, null, limit);
    }

    public RedditPostListing getPosts(String subreddit, String after, int limit) throws IOException, InterruptedException {
        AuthToken authToken = getAuthToken();
        String url;
        if(after == null){
            url = String.format("%s/r/%s/new.json?limit=%d", apiBaseUrl, subreddit, limit);
        }else{
            url = String.format("%s/r/%s/new.json?after=%s&limit=%d", apiBaseUrl, subreddit, after, limit);
        }
        HttpRequest userRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("User-Agent", "rdating/0.1 (by /u/Food-Little)")
                .header("Authorization", "Bearer " + authToken.accessToken())
                .build();
        HttpResponse<String> response = httpClient.send(userRequest, HttpResponse.BodyHandlers.ofString());
        return gson.fromJson(response.body(), RedditPostListing.class);
    }

    public List<RedditCommentArray.Listing> getComments(String subreddit, String postId, int limit) throws IOException, InterruptedException {
        return getComments(subreddit, postId, null, limit);
    }

    public List<RedditCommentArray.Listing> getComments(String subreddit, String postId, String after, int limit) throws IOException, InterruptedException {
        AuthToken authToken = getAuthToken();
        String url;
        if(after == null){
            url = String.format("%s/r/%s/comments/%s.json?limit=%s", apiBaseUrl, subreddit, postId, limit);
        }else{
            url = String.format("%s/r/%s/comments/%s.json?after=%s&limit=%s", apiBaseUrl, subreddit, postId, after, limit);
        }
        HttpRequest userRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("User-Agent", "rdating/0.1 (by /u/Food-Little)")
                .header("Authorization", "Bearer " + authToken.accessToken())
                .build();
        HttpResponse<String> response = httpClient.send(userRequest, HttpResponse.BodyHandlers.ofString());
        return RedditCommentArray.fromJson(response.body(), gson);
    }

    private AuthToken getAuthToken() throws IOException, InterruptedException {
        if(authToken == null || authToken.expired()) {
            authToken = getAuthTokenFromOrigin();
        }
        return authToken;
    }

    private AuthToken getAuthTokenFromOrigin() throws IOException, InterruptedException {
        String credentials = Base64
                .getEncoder()
                .encodeToString(
                        String
                        .format("%s:%s", clientId, secret)
                        .getBytes(StandardCharsets.UTF_8));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(authUrl))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("User-Agent", userAgent)
                .header("Authorization", String.format("Basic %s", credentials))
                .POST(HttpRequest.BodyPublishers.ofString(String.format("grant_type=password&username=%s&password=%s", userName, password)))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        String responseBody = response.body();
        JsonNode jsonNode = new ObjectMapper().readTree(responseBody);
        String accessToken = jsonNode.get("access_token").asText();
        DecodedJWT jwt = JWT.decode(accessToken);
        return new AuthToken(accessToken, jwt.getExpiresAtAsInstant());
    }

}
