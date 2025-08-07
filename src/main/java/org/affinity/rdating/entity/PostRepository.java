/*
  Copyright 2025, RDating. All rights reserved.
*/
package org.affinity.rdating.entity;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<PostEntity, Long> {

  List<PostEntity> findAllByisNSFWTrue();

  Optional<PostEntity> findByPostId(String postId);
}
