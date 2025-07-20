package org.affinity.rdating.client;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.affinity.rdating.enums.ListingKind;
import org.affinity.rdating.model.Author;
import org.affinity.rdating.model.Comment;
import org.affinity.rdating.model.Post;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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

    public List<Post> getPosts(String subreddit, int limit) throws IOException, InterruptedException {
        return getPosts(subreddit, null, limit);
    }

    public List<Post> getPosts(String subreddit, String after, int limit) throws IOException, InterruptedException {
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
        RedditPostListing redditPostListing = gson.fromJson(response.body(), RedditPostListing.class);
        List<Post> posts = new ArrayList<>();
        for (RedditPostListing.Child child : redditPostListing.data.children) {
            posts.add(new Post(
                    child.data.id,
                    child.data.title,
                    child.data.permalink,
                    new Author(child.data.author)));
        }
        return posts;
    }

    public List<Comment> getComments(String subreddit, String postId, int limit) throws IOException, InterruptedException {
        return getComments(subreddit, postId, null, limit);
    }

    public List<Comment> getComments(String subreddit, String postId, String after, int limit) throws IOException, InterruptedException {
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
        List<RedditCommentArray.Listing> redditComments = RedditCommentArray.fromJson(response.body(), gson);
        List<Comment> comments = new ArrayList<>();
        redditComments.forEach(
                listing -> listing.data.children.forEach(
                        child -> {
                            if(ListingKind.fromKind(child.kind) == ListingKind.COMMENT){
                                JsonObject commentData = child.data;
                                comments.add(new Comment(
                                        commentData.get("id").getAsString(),
                                        commentData.get("body").getAsString(),
                                        new Author(commentData.get("author").getAsString())
                                ));
                            }
                        }
                )
        );
        return comments;
    }

    public void sendMessage(String subreddit, String recipient, String subject, String text) throws IOException, InterruptedException {
        String postBody = String.format("api_type=json" +
                "&from_sr=%s" +
                "&subject=%s" +
                "&text=%s" +
                "&to=%s",
                subreddit,
                subject,
                text,
                recipient);
        AuthToken authToken = getAuthToken();
        HttpRequest userRequest = HttpRequest.newBuilder()
                .uri(URI.create(String.format("%s%s", apiBaseUrl, "/api/compose")))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("User-Agent", userAgent)
                .header("Authorization", "Bearer " + authToken.accessToken())
                .POST(HttpRequest.BodyPublishers.ofString(postBody))
                .build();
        httpClient.send(userRequest, HttpResponse.BodyHandlers.ofString());
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
