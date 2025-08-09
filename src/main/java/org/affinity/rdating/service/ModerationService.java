/*
  Copyright 2025, RDating. All rights reserved.
*/
package org.affinity.rdating.service;

import java.io.IOException;
import org.affinity.rdating.model.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ModerationService {

  private static final Logger logger = LoggerFactory.getLogger(ModerationService.class);

  @Value("${reddit.baseUrl}")
  private String redditUrl;

  private final PostService postService;
  private final UserService userService;

  public ModerationService(PostService postService, UserService userService) {
    this.postService = postService;
    this.userService = userService;
  }

  public void removeNsfw(String subreddit) throws IOException, InterruptedException {
    postService.getPostsFromRedditNewestFirst(subreddit).stream()
        .filter(Post::isNSFW)
        .forEach(
            post -> {
              try {
                postService.removePost(post.getId(), "nsfw");
              } catch (IOException | InterruptedException e) {
                logger.error("Failed to remove NSFW post: {}", post.getId(), e);
              }
            });
  }

  public void removeDuplicates(String subreddit) throws IOException, InterruptedException {
    postService
        .getDuplicatePosts(subreddit)
        .forEach(
            post -> {
              try {
                postService.removePost(post.getId(), "duplicate");
              } catch (IOException | InterruptedException e) {
                logger.error("Failed to remove duplicate post: {}", post.getId(), e);
              }
            });
  }
}
