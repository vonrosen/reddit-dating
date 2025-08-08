/*
  Copyright 2025, RDating. All rights reserved.
*/
package org.affinity.rdating.service;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.affinity.rdating.client.RedditClient;
import org.affinity.rdating.entity.PostRepository;
import org.affinity.rdating.model.Author;
import org.affinity.rdating.model.Post;
import org.affinity.rdating.model.PostsAndAfter;
import org.springframework.stereotype.Service;

@Service
public class PostService {

  private static final int POST_LIMIT = 50;

  private final RedditClient redditClient;
  private final PostRepository postRepository;

  public PostService(RedditClient redditClient, PostRepository postRepository) {
    this.redditClient = redditClient;
    this.postRepository = postRepository;
  }

  public List<Post> getPostsFromRedditNewestFirst(String subreddit)
      throws IOException, InterruptedException {
    List<Post> posts = new ArrayList<>();
    String after = null;
    do {
      PostsAndAfter postsAndAfter = redditClient.getPostsNewestFirst(subreddit, after, POST_LIMIT);
      posts.addAll(postsAndAfter.posts());
      after = postsAndAfter.after();
    } while (after != null);
    return posts;
  }

  public List<Post> getDuplicatePosts(String subreddit) throws IOException, InterruptedException {
    Map<Author, List<Post>> authorToPosts =
        getPostsFromRedditNewestFirst(subreddit).stream()
            .collect(Collectors.groupingBy(Post::getAuthor));
    List<Post> dupes = new ArrayList<>();
    for (Author author : authorToPosts.keySet()) {
      List<Post> posts = authorToPosts.get(author);
      while (posts.size() > 1) {
        dupes.add(posts.removeFirst());
      }
    }
    return dupes;
  }

  public void removePost(String postId) throws IOException, InterruptedException {
    redditClient.removePost(postId);
    postRepository
        .findByPostId(postId)
        .ifPresent(
            postEntity -> {
              postEntity.setDeletedAt(Instant.now());
              postRepository.save(postEntity);
            });
  }
}
