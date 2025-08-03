/*
  Copyright 2025, RDating. All rights reserved.
*/
package org.affinity.rdating.client;

import java.time.Instant;
import org.affinity.rdating.model.Author;

public class UserAuthToken extends AuthToken {

  private final Author author;
  private final String refreshToken;
  private final String scope;
  private final String tokenType;

  public UserAuthToken(
      Author author,
      String accessToken,
      String refreshToken,
      String scope,
      String tokenType,
      Instant expires) {
    super(accessToken, expires);
    this.author = author;
    this.refreshToken = refreshToken;
    this.scope = scope;
    this.tokenType = tokenType;
  }

  public String getTokenType() {
    return tokenType;
  }

  public String getScope() {
    return scope;
  }

  public String getRefreshToken() {
    return refreshToken;
  }

  public Author getAuthor() {
    return author;
  }
}
