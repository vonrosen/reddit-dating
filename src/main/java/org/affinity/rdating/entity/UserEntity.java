/*
  Copyright 2025, RDating. All rights reserved.
*/
package org.affinity.rdating.entity;

import jakarta.persistence.*;
import java.time.Instant;
import org.affinity.rdating.client.UserAuthToken;
import org.affinity.rdating.converter.SensitiveDataConverter;
import org.affinity.rdating.model.Author;

@Entity
@Table(name = "user")
public class UserEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(mappedBy = "user")
  private PostEntity post;

  @Column(name = "user_name", nullable = false, unique = true)
  private String userName;

  @Convert(converter = SensitiveDataConverter.class)
  @Column(name = "auth_token", columnDefinition = "TEXT")
  private String authToken;

  @Column(name = "auth_token_type")
  private String authTokenType;

  @Column(name = "auth_token_scope")
  private String authTokenScope;

  @Column(name = "auth_token_expires_at")
  private Instant authTokenExpiresAt;

  @Convert(converter = SensitiveDataConverter.class)
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

  @Transient
  public UserAuthToken getUserAuthToken() {
    return new UserAuthToken(
        new Author(userName),
        authToken,
        refreshToken,
        authTokenScope,
        authTokenType,
        authTokenExpiresAt);
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

  public String getAuthToken() {
    return authToken;
  }

  public void setAuthToken(String authToken) {
    this.authToken = authToken;
  }

  public String getRefreshToken() {
    return refreshToken;
  }

  public void setRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }

  public Instant getAuthTokenExpiresAt() {
    return authTokenExpiresAt;
  }

  public void setAuthTokenExpiresAt(Instant authTokenExpiresAt) {
    this.authTokenExpiresAt = authTokenExpiresAt;
  }

  public PostEntity getPost() {
    return post;
  }

  public void setPost(PostEntity post) {
    this.post = post;
  }

  public Instant getRegisteredAt() {
    return registeredAt;
  }

  public void setRegisteredAt(Instant registeredAt) {
    this.registeredAt = registeredAt;
  }
}
