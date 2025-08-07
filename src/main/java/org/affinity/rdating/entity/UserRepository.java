/*
  Copyright 2025, RDating. All rights reserved.
*/
package org.affinity.rdating.entity;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

  List<UserEntity> findByRegistrationMessageSentAtIsNull();

  List<UserEntity> findByRegisteredAtIsNotNull();

  Optional<UserEntity> findByAuthRequestStateToken(String authRequestStateToken);

  Optional<UserEntity> findByUserName(String userName);

  void deleteByUserName(String userName);
}
