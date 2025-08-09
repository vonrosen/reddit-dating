/*
  Copyright 2025, RDating. All rights reserved.
*/
package org.affinity.rdating.client;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import org.affinity.rdating.enums.ListingKind;
import org.affinity.rdating.metric.CounterEnabled;
import org.affinity.rdating.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RedditClient {
  private static final Logger logger = LoggerFactory.getLogger(RedditClient.class);

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

  @Value("${reddit.redirectUrl}")
  private String redirectUrl;

  private AuthToken authToken;

  public RedditClient(Gson gson) {
    this.gson = gson;
  }

  @CounterEnabled
  public PostsAndAfter getPostsNewestFirst(String subreddit, String after, int limit)
      throws IOException, InterruptedException {
    AuthToken authToken = getAuthToken();
    String url;
    if (after == null) {
      url = String.format("%s/r/%s/new.json?limit=%d", apiBaseUrl, subreddit, limit);
    } else {
      url =
          String.format("%s/r/%s/new.json?after=%s&limit=%d", apiBaseUrl, subreddit, after, limit);
    }
    HttpRequest userRequest =
        HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("User-Agent", userAgent)
            .header("Authorization", "Bearer " + authToken.getAccessToken())
            .build();
    HttpResponse<String> response =
        httpClient.send(userRequest, HttpResponse.BodyHandlers.ofString());
    RedditPostListing redditPostListing = gson.fromJson(response.body(), RedditPostListing.class);
    List<Post> posts = new ArrayList<>();
    for (RedditPostListing.Child child : redditPostListing.data.children) {
      posts.add(
          new Post(
              new PostId(child.data.id),
              child.data.title,
              child.data.permalink,
              new Author(child.data.author),
              child.data.over_18));
    }
    return new PostsAndAfter(posts, redditPostListing.data.after);
  }

  @CounterEnabled
  public void sendMessage(String subreddit, String recipient, String subject, String text)
      throws IOException, InterruptedException {
    String postBody =
        String.format(
            "api_type=json" + "&from_sr=%s" + "&subject=%s" + "&text=%s" + "&to=%s",
            subreddit, subject, text, recipient);
    AuthToken authToken = getAuthToken();
    HttpRequest userRequest =
        HttpRequest.newBuilder()
            .uri(URI.create(String.format("%s%s", apiBaseUrl, "/api/compose")))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .header("User-Agent", userAgent)
            .header("Authorization", "Bearer " + authToken.getAccessToken())
            .POST(HttpRequest.BodyPublishers.ofString(postBody))
            .build();
    httpClient.send(userRequest, HttpResponse.BodyHandlers.ofString());
  }

  @CounterEnabled
  public void removePost(PostId postId, String reason) throws IOException, InterruptedException {
    AuthToken authToken = getAuthToken();
    HttpRequest userRequest =
        HttpRequest.newBuilder()
            .uri(URI.create(String.format("%s%s", apiBaseUrl, "/api/remove?raw_json=1")))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .header("User-Agent", userAgent)
            .header("Authorization", "Bearer " + authToken.getAccessToken())
            .POST(
                HttpRequest.BodyPublishers.ofString(
                    String.format(
                        "api_type=json&id=%s&spam=false&reason=%s", postId.fullName(), reason)))
            .build();
    HttpResponse<String> response =
        httpClient.send(userRequest, HttpResponse.BodyHandlers.ofString());
    logger.info("Attempted to remove post {}: Response code {}", postId, response.statusCode());
  }

  @CounterEnabled
  public UpvotesAndAfter getUpvotes(UserAuthToken userAuthToken, String after, int limit)
      throws IOException, InterruptedException {
    String uri;
    if (after == null) {
      uri =
          String.format(
              "%s/user/%s/upvoted?limit=%d",
              apiBaseUrl, userAuthToken.getAuthor().username(), limit);
    } else {
      uri =
          String.format(
              "%s/user/%s/upvoted?after=%s&limit=%d",
              apiBaseUrl, userAuthToken.getAuthor().username(), after, limit);
    }
    HttpRequest userRequest =
        HttpRequest.newBuilder()
            .uri(URI.create(uri))
            .header("User-Agent", userAgent)
            .header("Authorization", String.format("Bearer %s", userAuthToken.getAccessToken()))
            .build();
    HttpResponse<String> response =
        httpClient.send(userRequest, HttpResponse.BodyHandlers.ofString());
    RedditUpvoteListing redditUpvoteListing =
        gson.fromJson(response.body(), RedditUpvoteListing.class);
    List<Upvote> upvotes = new ArrayList<>();
    for (RedditUpvoteListing.RedditChild child : redditUpvoteListing.data.children) {
      if (ListingKind.fromKind(child.kind) == ListingKind.POST) {
        upvotes.add(
            new Upvote(
                new Author(child.data.author),
                child.data.subreddit,
                child.data.title,
                child.data.id));
      }
    }
    return new UpvotesAndAfter(upvotes, redditUpvoteListing.data.after);
  }

  @CounterEnabled
  public UserAuthToken getUserAuthTokenFromRefreshToken(UserAuthToken userAuthToken)
      throws IOException, InterruptedException {
    String credentials =
        Base64.getEncoder()
            .encodeToString(
                String.format("%s:%s", clientId, secret).getBytes(StandardCharsets.UTF_8));
    HttpRequest request =
        HttpRequest.newBuilder()
            .uri(URI.create(authUrl))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .header("User-Agent", userAgent)
            .header("Authorization", String.format("Basic %s", credentials))
            .POST(
                HttpRequest.BodyPublishers.ofString(
                    String.format(
                        "grant_type=refresh_token&refresh_token=%s",
                        userAuthToken.getRefreshToken())))
            .build();

    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    String responseBody = response.body();
    JsonNode jsonNode = new ObjectMapper().readTree(responseBody);
    String accessToken = jsonNode.get("access_token").asText();
    String tokenType = jsonNode.get("token_type").asText();
    String scope = jsonNode.get("scope").asText();

    DecodedJWT jwt = JWT.decode(accessToken);
    return new UserAuthToken(
        userAuthToken.getAuthor(),
        accessToken,
        userAuthToken.getRefreshToken(),
        scope,
        tokenType,
        jwt.getExpiresAtAsInstant());
  }

  @CounterEnabled
  public UserAuthToken getUserAuthTokenFromCode(String code, Author author)
      throws IOException, InterruptedException {
    String credentials =
        Base64.getEncoder()
            .encodeToString(
                String.format("%s:%s", clientId, secret).getBytes(StandardCharsets.UTF_8));

    HttpRequest request =
        HttpRequest.newBuilder()
            .uri(URI.create(authUrl))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .header("User-Agent", userAgent)
            .header("Authorization", String.format("Basic %s", credentials))
            .POST(
                HttpRequest.BodyPublishers.ofString(
                    String.format(
                        "grant_type=authorization_code&code=%s&redirect_uri=%s",
                        code, redirectUrl)))
            .build();

    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    String responseBody = response.body();
    JsonNode jsonNode = new ObjectMapper().readTree(responseBody);
    String accessToken = jsonNode.get("access_token").asText();
    String tokenType = jsonNode.get("token_type").asText();
    String refreshToken = jsonNode.get("refresh_token").asText();
    String scope = jsonNode.get("scope").asText();

    DecodedJWT jwt = JWT.decode(accessToken);
    return new UserAuthToken(
        author, accessToken, refreshToken, scope, tokenType, jwt.getExpiresAtAsInstant());
  }

  private AuthToken getAuthToken() throws IOException, InterruptedException {
    if (authToken == null || authToken.expired()) {
      authToken = getAuthTokenFromOrigin();
    }
    return authToken;
  }

  @CounterEnabled
  private AuthToken getAuthTokenFromOrigin() throws IOException, InterruptedException {
    logger.info("Fetching new auth token from Reddit");
    String credentials =
        Base64.getEncoder()
            .encodeToString(
                String.format("%s:%s", clientId, secret).getBytes(StandardCharsets.UTF_8));

    HttpRequest request =
        HttpRequest.newBuilder()
            .uri(URI.create(authUrl))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .header("User-Agent", userAgent)
            .header("Authorization", String.format("Basic %s", credentials))
            .POST(
                HttpRequest.BodyPublishers.ofString(
                    String.format(
                        "grant_type=password&username=%s&password=%s", userName, password)))
            .build();

    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    String responseBody = response.body();
    JsonNode jsonNode = new ObjectMapper().readTree(responseBody);
    String accessToken = jsonNode.get("access_token").asText();
    DecodedJWT jwt = JWT.decode(accessToken);
    return new AuthToken(accessToken, jwt.getExpiresAtAsInstant());
  }
}
