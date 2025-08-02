/*
  Copyright 2025, RDating. All rights reserved.
*/
package org.affinity.rdating.entity;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

  List<UserEntity> findByRegistrationMessageSentAtIsNull();
}
