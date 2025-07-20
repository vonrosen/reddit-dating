package org.affinity.rdating.client;

import java.time.Instant;

public record AuthToken(String accessToken, Instant expires) {
    private static final long BUFFER_BEFORE_EXPIRATION_MILLIS = 60_1000L;

    boolean expired(){
        return expires.isBefore(Instant.now().minusSeconds(BUFFER_BEFORE_EXPIRATION_MILLIS));
    }

}
