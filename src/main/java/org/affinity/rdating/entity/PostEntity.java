/*
  Copyright 2025, RDating. All rights reserved.
*/
package org.affinity.rdating.entity;

import jakarta.persistence.*;
import java.time.Instant;
import org.affinity.rdating.model.Author;
import org.affinity.rdating.model.Post;
import org.affinity.rdating.model.PostId;

@Entity
@Table(
    name = "post",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id"})})
public class PostEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "user_id", referencedColumnName = "id")
  private UserEntity user;

  @Column(name = "post_id", nullable = false)
  private String postId;

  @Column(name = "title", nullable = false, columnDefinition = "TEXT")
  private String title;

  @Column(name = "permalink", nullable = false)
  private String permalink;

  @Column(name = "is_nsfw")
  private Boolean isNSFW = false;

  @Column(name = "deleted_at")
  private Instant deletedAt;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  public PostEntity() {}

  public PostEntity(UserEntity user, String postId, String title, String permalink) {
    this.user = user;
    this.postId = postId;
    this.title = title;
    this.permalink = permalink;
    this.createdAt = Instant.now();
    this.updatedAt = Instant.now();
  }

  public Long getId() {
    return id;
  }

  public UserEntity getUser() {
    return user;
  }

  public void setUser(UserEntity user) {
    this.user = user;
  }

  public String getPostId() {
    return postId;
  }

  public void setPostId(String postId) {
    this.postId = postId;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getPermalink() {
    return permalink;
  }

  public void setPermalink(String permalink) {
    this.permalink = permalink;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Instant updatedAt) {
    this.updatedAt = updatedAt;
  }

  @Transient
  public Post toPost() {
    return new Post(new PostId(postId), title, permalink, new Author(user.getUserName()), isNSFW);
  }

  public Boolean getNSFW() {
    return isNSFW;
  }

  public void setNSFW(Boolean NSFW) {
    isNSFW = NSFW;
  }

  public Instant getDeletedAt() {
    return deletedAt;
  }

  public void setDeletedAt(Instant deletedAt) {
    this.deletedAt = deletedAt;
  }
}
