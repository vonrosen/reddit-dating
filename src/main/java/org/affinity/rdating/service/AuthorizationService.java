/*
  Copyright 2025, RDating. All rights reserved.
*/
package org.affinity.rdating.service;

import java.io.IOException;
import org.affinity.rdating.client.RedditClient;
import org.affinity.rdating.client.UserAuthToken;
import org.affinity.rdating.entity.UserEntity;
import org.affinity.rdating.entity.UserRepository;
import org.affinity.rdating.model.Author;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService {

  private final RedditClient redditClient;
  private final UserRepository userRepository;

  public AuthorizationService(RedditClient redditClient, UserRepository userRepository) {
    this.redditClient = redditClient;
    this.userRepository = userRepository;
  }

  public UserAuthToken getUserAuthToken(Author author) throws IOException, InterruptedException {
    UserEntity userEntity = userRepository.findByUserName(author.username()).orElseThrow();
    if (!userEntity.getUserAuthToken().expired()) {
      return userEntity.getUserAuthToken();
    }
    UserAuthToken userAuthToken =
        redditClient.getUserAuthTokenFromRefreshToken(userEntity.getUserAuthToken());
    userEntity.setAuthToken(userAuthToken.getAccessToken());
    userEntity.setAuthTokenExpiresAt(userAuthToken.getExpires());
    userRepository.save(userEntity);
    return userAuthToken;
  }
}
