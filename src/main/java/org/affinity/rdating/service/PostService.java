/* (C)2025 */
package org.affinity.rdating.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.affinity.rdating.client.RedditClient;
import org.affinity.rdating.model.Post;
import org.affinity.rdating.model.PostsAndAfter;
import org.springframework.stereotype.Service;

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
      PostsAndAfter postsAndAfter = redditClient.getPosts(subreddit, after, POST_LIMIT);
      posts.addAll(postsAndAfter.getPosts());
      after = postsAndAfter.getAfter();
    } while (after != null);
    return posts;
  }
}
