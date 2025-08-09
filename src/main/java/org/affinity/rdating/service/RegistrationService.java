/*
  Copyright 2025, RDating. All rights reserved.
*/
package org.affinity.rdating.service;

import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.affinity.rdating.client.RedditClient;
import org.affinity.rdating.client.UserAuthToken;
import org.affinity.rdating.entity.PostEntity;
import org.affinity.rdating.entity.PostRepository;
import org.affinity.rdating.entity.UserEntity;
import org.affinity.rdating.entity.UserRepository;
import org.affinity.rdating.model.Author;
import org.affinity.rdating.model.Post;
import org.affinity.rdating.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {
  private static final Logger logger = LoggerFactory.getLogger(RegistrationService.class);

  private final PostService postService;
  private final UserRepository userRepository;
  private final PostRepository postRepository;
  private final NotificationService notificationService;
  private final RedditClient redditClient;

  public RegistrationService(
      PostService postService,
      UserRepository userRepository,
      PostRepository postRepository,
      NotificationService notificationService,
      RedditClient redditClient) {
    this.postService = postService;
    this.userRepository = userRepository;
    this.postRepository = postRepository;
    this.notificationService = notificationService;
    this.redditClient = redditClient;
  }

  public void addUsers(String subreddit) throws IOException, InterruptedException {
    Set<Post> posts =
        postService.getPostsFromRedditNewestFirst(subreddit).stream()
            .filter(CollectionUtils.distinctByKey(Post::getAuthor))
            .collect(Collectors.toSet());
    logger.info("Fetched {} posts from subreddit: {}", posts.size(), subreddit);
    Map<Author, UserEntity> authorToUser =
        userRepository.findAll().stream()
            .collect(
                Collectors.toMap(
                    userEntity -> new Author(userEntity.getUserName()), Function.identity()));
    logger.info("Fetched {} users", authorToUser.size());
    posts.stream()
        .filter(post -> !authorToUser.containsKey(post.getAuthor()))
        .forEach(
            post -> {
              UserEntity userEntity = new UserEntity(post.getAuthor().username());
              userRepository.save(userEntity);
              PostEntity postEntity =
                  new PostEntity(
                      userEntity, post.getId().id(), post.getTitle(), post.getPermaLink());
              postRepository.save(postEntity);
              logger.info("Added new user: {}", post.getAuthor().username());
            });
  }

  public void sendRegistrationMessages(String subreddit) {
    userRepository
        .findByRegistrationMessageSentAtIsNull()
        .forEach(
            userEntity -> {
              try {
                UUID uuid = UUID.randomUUID();
                notificationService.sendRegistrationMessage(
                    subreddit, new Author(userEntity.getUserName()), uuid);
                userEntity.setRegistrationMessageSentAt(Instant.now());
                userEntity.setAuthRequestStateToken(uuid.toString());
                userRepository.save(userEntity);
                logger.info("Sent registration message to user: {}", userEntity.getUserName());
              } catch (IOException | InterruptedException e) {
                logger.error(
                    "Failed to send registration message to user: {}", userEntity.getUserName(), e);
              }
            });
  }

  public void register(String subreddit, String state, String code)
      throws IOException, InterruptedException {
    UserEntity userEntity = userRepository.findByAuthRequestStateToken(state).orElseThrow();
    UserAuthToken userAuthToken =
        redditClient.getUserAuthTokenFromCode(code, new Author(userEntity.getUserName()));
    userEntity.setAuthToken(userAuthToken.getAccessToken());
    userEntity.setRefreshToken(userAuthToken.getRefreshToken());
    userEntity.setAuthTokenExpiresAt(userAuthToken.getExpires());
    userEntity.setRegisteredAt(Instant.now());
    userRepository.save(userEntity);
    notificationService.sendRegisteredMessage(subreddit, new Author(userEntity.getUserName()));
    logger.info("User {} registered successfully", userEntity.getUserName());
  }
}
