package org.affinity.rdating.service;

import org.affinity.rdating.client.RedditClient;
import org.affinity.rdating.model.Post;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class PostService {

    private static final int POST_LIMIT = 1000;

    private final RedditClient redditClient;

    public PostService(RedditClient redditClient) {
        this.redditClient = redditClient;
    }

    public List<Post> getPosts(String subreddit) throws IOException, InterruptedException {
        List<Post> posts = new ArrayList<>();
        String after = null;
        do {
            List<Post> fetchedPosts = redditClient.getPosts(subreddit, after, POST_LIMIT);
            if(fetchedPosts.isEmpty()){
                return posts;
            }
            posts.addAll(fetchedPosts);
            after = fetchedPosts.getLast().getId();
        } while(posts.size() == POST_LIMIT);
        return posts;
    }
}
