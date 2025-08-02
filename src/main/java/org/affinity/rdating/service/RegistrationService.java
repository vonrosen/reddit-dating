/*
  Copyright 2025, RDating. All rights reserved.
*/
package org.affinity.rdating.service;

import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.affinity.rdating.entity.UserEntity;
import org.affinity.rdating.entity.UserRepository;
import org.affinity.rdating.model.Author;
import org.affinity.rdating.model.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {
  private static final Logger logger = LoggerFactory.getLogger(RegistrationService.class);

  private final PostService postService;
  private final UserRepository userRepository;
  private final NotificationService notificationService;

  public RegistrationService(
      PostService postService,
      UserRepository userRepository,
      NotificationService notificationService) {
    this.postService = postService;
    this.userRepository = userRepository;
    this.notificationService = notificationService;
  }

  public void addUsers(String subreddit) throws IOException, InterruptedException {
    List<Post> posts = postService.getPosts(subreddit);
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

  public void register(String subreddit, String state, String code) {}
}
