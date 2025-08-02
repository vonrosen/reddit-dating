/*
  Copyright 2025, RDating. All rights reserved.
*/
package org.affinity.rdating.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.affinity.rdating.client.RedditClient;
import org.affinity.rdating.model.Comment;
import org.affinity.rdating.model.Post;
import org.springframework.stereotype.Service;

@Service
public class CommentService {

  private static final int COMMENT_LIMIT = 1000;

  private final RedditClient redditClient;

  public CommentService(RedditClient redditClient) {
    this.redditClient = redditClient;
  }

  public List<Comment> getComments(String subreddit, Post post)
      throws IOException, InterruptedException {
    List<Comment> comments = new ArrayList<>();
    String after = null;
    do {
      List<Comment> fetchedComments =
          redditClient.getComments(subreddit, post.getId(), after, COMMENT_LIMIT);
      if (fetchedComments.isEmpty()) {
        return comments;
      }
      comments.addAll(fetchedComments);
      after = fetchedComments.getLast().id();
    } while (comments.size() == COMMENT_LIMIT);
    return comments;
  }
}
