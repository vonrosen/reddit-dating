/*
  Copyright 2025, RDating. All rights reserved.
*/
package org.affinity.rdating.entity;

import java.time.Instant;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

public interface RedditMetricRepository extends CrudRepository<RedditMetricEntity, Long> {
  @Transactional
  @Modifying
  @Query(
      value =
          "INSERT INTO reddit_metric (hour, count) VALUES (?1, ?2) ON DUPLICATE KEY UPDATE count = count + ?2",
      nativeQuery = true)
  void upsertByHour(Instant hour, int count);
}
