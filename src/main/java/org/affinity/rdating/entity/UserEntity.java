/*
  Copyright 2025, RDating. All rights reserved.
*/
package org.affinity.rdating.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "user")
public class UserEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "user_name", nullable = false, unique = true)
  private String userName;

  @Column(name = "auth_token", columnDefinition = "TEXT")
  private String authToken;

  @Column(name = "auth_token_expires_at")
  private Instant authTokenExpiresAt;

  @Column(name = "refresh_token", columnDefinition = "TEXT")
  private String refreshToken;

  @Column(name = "auth_request_state_token", columnDefinition = "TEXT")
  private String authRequestStateToken;

  @Column(name = "registration_message_sent_at")
  private Instant registrationMessageSentAt;

  @Column(name = "registered_at")
  private Instant registeredAt;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @Column(name = "updated_at")
  private Instant updatedAt;

  public UserEntity() {}

  public UserEntity(String userName) {
    this.userName = userName;
    this.createdAt = Instant.now();
    this.updatedAt = Instant.now();
  }

  public String getUserName() {
    return userName;
  }

  public Instant getRegistrationMessageSentAt() {
    return registrationMessageSentAt;
  }

  public void setRegistrationMessageSentAt(Instant registrationMessageSentAt) {
    this.registrationMessageSentAt = registrationMessageSentAt;
  }

  public String getAuthRequestStateToken() {
    return authRequestStateToken;
  }

  public void setAuthRequestStateToken(String authRequestStateToken) {
    this.authRequestStateToken = authRequestStateToken;
  }
}
