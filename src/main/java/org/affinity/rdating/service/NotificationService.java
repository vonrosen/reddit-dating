/*
  Copyright 2025, RDating. All rights reserved.
*/
package org.affinity.rdating.service;

import java.io.IOException;
import java.util.UUID;
import org.affinity.rdating.client.RedditClient;
import org.affinity.rdating.model.Author;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

  private final RedditClient redditClient;
  private final MessageContentService messageContentService;

  public NotificationService(
      RedditClient redditClient, MessageContentService messageContentService) {
    this.redditClient = redditClient;
    this.messageContentService = messageContentService;
  }

  public void sendMatchMessage(String subreddit, Author to, Author match, String matchPostLink)
      throws IOException, InterruptedException {
    redditClient.sendMessage(
        subreddit,
        to.username(),
        messageContentService.getMessageSubject(),
        messageContentService.createMatchMessageContent(match.username(), matchPostLink));
  }

  public void sendRegistrationMessage(String subreddit, Author to, UUID stateToken)
      throws IOException, InterruptedException {
    redditClient.sendMessage(
        subreddit,
        to.username(),
        messageContentService.getRegistrationMessageSubject(),
        messageContentService.createRegistrationMessageContent(to, stateToken));
  }
}
