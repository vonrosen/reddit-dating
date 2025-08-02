/*
  Copyright 2025, RDating. All rights reserved.
*/
package org.affinity.rdating.model;

import java.util.List;
import java.util.Objects;

public class Post implements Comparable<Post> {

  private final String id;
  private final String title;
  private final Author author;
  private final String permaLink;
  private final List<Comment> comments;

  public Post(String id, String title, String permaLink, Author author) {
    this.id = id;
    this.title = title;
    this.author = author;
    this.permaLink = permaLink;
    this.comments = List.of();
  }

  public String getId() {
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

  public void addComment(Comment comment) {
    comments.add(comment);
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
}
