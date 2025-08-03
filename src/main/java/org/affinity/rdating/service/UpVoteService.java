/*
  Copyright 2025, RDating. All rights reserved.
*/
package org.affinity.rdating.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.affinity.rdating.client.RedditClient;
import org.affinity.rdating.client.UserAuthToken;
import org.affinity.rdating.entity.UserEntity;
import org.affinity.rdating.entity.UserRepository;
import org.affinity.rdating.model.*;
import org.springframework.stereotype.Service;

@Service
public class UpVoteService {

  private final RedditClient redditClient;
  private final UserRepository userRepository;

  public UpVoteService(RedditClient redditClient, UserRepository userRepository) {
    this.redditClient = redditClient;
    this.userRepository = userRepository;
  }

  public List<Upvote> getUpvotes(Author author, String subreddit)
      throws IOException, InterruptedException {
    UserEntity userEntity = userRepository.findByUserName(author.username()).orElseThrow();
    UserAuthToken userAuthToken = getUserAuthToken(userEntity);
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

  private UserAuthToken getUserAuthToken(UserEntity userEntity)
      throws IOException, InterruptedException {
    if (userEntity.getUserAuthToken().expired()) {
      UserAuthToken userAuthToken =
          redditClient.getUserAuthTokenFromRefreshToken(userEntity.getUserAuthToken());
      userEntity.setAuthToken(userAuthToken.getAccessToken());
      userEntity.setAuthTokenExpiresAt(userAuthToken.getExpires());
      userRepository.save(userEntity);
      return userAuthToken;
    }
    return userEntity.getUserAuthToken();
  }
}
