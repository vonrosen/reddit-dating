/*
  Copyright 2025, RDating. All rights reserved.
*/
package org.affinity.rdating.client;

import java.time.Instant;

public class AuthToken {
  private static final long BUFFER_BEFORE_EXPIRATION_SECONDS = 60L;

  private final String accessToken;
  private final Instant expires;

  public AuthToken(String accessToken, Instant expires) {
    this.accessToken = accessToken;
    this.expires = expires;
  }

  public Instant getExpires() {
    return expires;
  }

  public String getAccessToken() {
    return accessToken;
  }

  public boolean expired() {
    return expires.isBefore(Instant.now().minusSeconds(BUFFER_BEFORE_EXPIRATION_SECONDS));
  }
}
