/*
  Copyright 2025, RDating. All rights reserved.
*/
package org.affinity.rdating.model;

import java.util.Objects;

public class Post implements Comparable<Post> {

  private final PostId id;
  private final String title;
  private final Author author;
  private final String permaLink;
  private final boolean isNSFW;

  public Post(PostId id, String title, String permaLink, Author author, boolean isNSFW) {
    this.id = id;
    this.title = title;
    this.author = author;
    this.permaLink = permaLink;
    this.isNSFW = isNSFW;
  }

  public PostId getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public String getPermaLink() {
    return permaLink;
  }

  public Author getAuthor() {
    return author;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    Post post = (Post) o;
    return Objects.equals(id, post.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }

  @Override
  public int compareTo(Post other) {
    return this.author.username().compareTo(other.author.username());
  }

  public boolean isNSFW() {
    return isNSFW;
  }
}
