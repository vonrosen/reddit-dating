/*
  Copyright 2025, RDating. All rights reserved.
*/
package org.affinity.rdating.service;

import org.affinity.rdating.entity.UserRepository;
import org.affinity.rdating.model.Author;
import org.affinity.rdating.model.User;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public User getUser(Author author) {
    return userRepository.findByUserName(author.username()).orElseThrow().toUser();
  }

  public void removeUser(Author author) {
    userRepository.deleteByUserName(author.username());
  }
}
