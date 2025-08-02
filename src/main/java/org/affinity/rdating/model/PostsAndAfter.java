/*
  Copyright 2025, RDating. All rights reserved.
*/
package org.affinity.rdating.model;

import java.util.List;

public class PostsAndAfter {

  private final List<Post> posts;
  private final String after;

  public PostsAndAfter(List<Post> posts, String after) {
    this.posts = posts;
    this.after = after;
  }

  public List<Post> getPosts() {
    return posts;
  }

  public String getAfter() {
    return after;
  }
}
