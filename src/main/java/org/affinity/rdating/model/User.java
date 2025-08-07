/*
  Copyright 2025, RDating. All rights reserved.
*/
package org.affinity.rdating.model;

import java.time.Instant;
import org.affinity.rdating.client.UserAuthToken;

public record User(
    Long id,
    String userName,
    UserAuthToken userAuthToken,
    String authRequestStateToken,
    Instant registrationMessageSentAt,
    Instant registeredAt,
    Instant createdAt,
    Instant updatedAt) {}
