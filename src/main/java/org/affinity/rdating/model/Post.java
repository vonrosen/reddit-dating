/*
  Copyright 2025, RDating. All rights reserved.
*/
package org.affinity.rdating.model;

import java.util.Objects;

public record Post(PostId id, String title, String permaLink, Author author, boolean isNSFW)
    implements Comparable<Post> {

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
