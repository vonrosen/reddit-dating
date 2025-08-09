/*
  Copyright 2025, RDating. All rights reserved.
*/
package org.affinity.rdating.service;

import java.io.IOException;
import org.affinity.rdating.model.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ModerationService {

  private static final Logger logger = LoggerFactory.getLogger(ModerationService.class);

  private final PostService postService;

  public ModerationService(PostService postService) {
    this.postService = postService;
  }

  public void removeNsfw(String subreddit) throws IOException, InterruptedException {
    postService.getPostsFromRedditNewestFirst(subreddit).stream()
        .filter(Post::isNSFW)
        .forEach(
            post -> {
              try {
                postService.removePost(post.id(), "nsfw");
              } catch (IOException | InterruptedException e) {
                logger.error("Failed to remove NSFW post: {}", post.id(), e);
              }
            });
  }

  public void removeDuplicates(String subreddit) throws IOException, InterruptedException {
    postService
        .getDuplicatePosts(subreddit)
        .forEach(
            post -> {
              try {
                postService.removePost(post.id(), "duplicate");
              } catch (IOException | InterruptedException e) {
                logger.error("Failed to remove duplicate post: {}", post.id(), e);
              }
            });
  }
}
