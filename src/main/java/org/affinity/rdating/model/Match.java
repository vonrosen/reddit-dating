/*
  Copyright 2025, RDating. All rights reserved.
*/
package org.affinity.rdating.model;

import java.util.Objects;

public record Match(Post post1, Post post2) {

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    Match match = (Match) o;
    return Objects.equals(post1.author(), match.post1.author())
        && Objects.equals(post2.author(), match.post2.author());
  }

  @Override
  public int hashCode() {
    return Objects.hash(post1.author(), post2.author());
  }
}
