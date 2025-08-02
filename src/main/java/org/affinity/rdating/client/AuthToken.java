/* (C)2025 */
package org.affinity.rdating.client;

import java.time.Instant;

public record AuthToken(String accessToken, Instant expires) {
  private static final long BUFFER_BEFORE_EXPIRATION_SECONDS = 60L;

  boolean expired() {
    return expires.isBefore(Instant.now().minusSeconds(BUFFER_BEFORE_EXPIRATION_SECONDS));
  }
}
