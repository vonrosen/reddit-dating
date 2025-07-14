package org.affinity.rdating.client;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.http.HttpClient;

@Service
public class RedditClient {
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Value("${reddit.client.password}")
    private String password;

    @Value("${reddit.client.id}")
    private String clientId;

    @Value("${reddit.client.secret}")
    private String secret;

    @Value("${reddit.client.username}")
    private String userName;

    @Value("${reddit.client.user-agent}")
    private String userAgent;

    @Value("${reddit.subreddit.name}")
    private String subredditName;

}
