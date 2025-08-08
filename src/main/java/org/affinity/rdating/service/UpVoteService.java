/*
  Copyright 2025, RDating. All rights reserved.
*/
package org.affinity.rdating.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.affinity.rdating.client.RedditClient;
import org.affinity.rdating.client.UserAuthToken;
import org.affinity.rdating.entity.UserRepository;
import org.affinity.rdating.model.*;
import org.springframework.stereotype.Service;

@Service
public class UpVoteService {

  private final RedditClient redditClient;
  private final AuthorizationService authorizationService;

  public UpVoteService(
      RedditClient redditClient,
      UserRepository userRepository,
      AuthorizationService authorizationService) {
    this.redditClient = redditClient;
    this.authorizationService = authorizationService;
  }

  public List<Upvote> getUpvotes(Author author, String subreddit)
      throws IOException, InterruptedException {
    UserAuthToken userAuthToken = authorizationService.getUserAuthToken(author);
    List<Upvote> upvotes = new ArrayList<>();
    String after = null;
    do {
      UpvotesAndAfter upvotesAndAfter = redditClient.getUpvotes(userAuthToken, after, 100);
      upvotesAndAfter.upvotes().stream()
          .filter(upvote -> upvote.subreddit().equals(subreddit))
          .filter(upvote -> !upvote.author().equals(author))
          .forEach(upvotes::add);
      after = upvotesAndAfter.after();
    } while (after != null);
    return upvotes;
  }
}
