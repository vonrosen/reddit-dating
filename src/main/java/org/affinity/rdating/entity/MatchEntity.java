/*
  Copyright 2025, RDating. All rights reserved.
*/
package org.affinity.rdating.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity(name = "matches")
public class MatchEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String user1;
  private String user2;

  @Column(name = "user1_post_url")
  private String user1PostUrl;

  @Column(name = "user2_post_url")
  private String user2PostUrl;

  @Column(name = "created_at")
  private Instant createdAt;

  public MatchEntity() {}

  public MatchEntity(
      String user1, String user2, String user1PostUrl, String user2PostUrl, Instant createdAt) {
    this.user1 = user1;
    this.user2 = user2;
    this.user1PostUrl = user1PostUrl;
    this.user2PostUrl = user2PostUrl;
    this.createdAt = createdAt;
  }

  public Long getId() {
    return id;
  }

  public String getUser1() {
    return user1;
  }

  public String getUser2() {
    return user2;
  }

  public String getUser1PostUrl() {
    return user1PostUrl;
  }

  public String getUser2PostUrl() {
    return user2PostUrl;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }
}
