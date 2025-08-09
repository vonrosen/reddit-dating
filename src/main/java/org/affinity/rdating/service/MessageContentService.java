/*
  Copyright 2025, RDating. All rights reserved.
*/
package org.affinity.rdating.service;

import java.util.UUID;
import org.affinity.rdating.model.Author;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MessageContentService {

  @Value("${reddit.subreddit.name}")
  private String subreddit;

  @Value("${reddit.baseUrl}")
  private String redditUrl;

  @Value("${reddit.client.id}")
  private String redditClientId;

  @Value("${reddit.redirectUrl}")
  private String redirectUrl;

  public String getMessageSubject() {
    return "New Match Notification from RDating";
  }

  public String createMatchMessageContent(String matchUsername, String matchPermalink) {
    return String.format(
        "You have a new match with %s! Check out their profile here: %s%s",
        matchUsername, redditUrl, matchPermalink);
  }

  public String getRegistrationMessageSubject() {
    return "Welcome to RDating!";
  }

  public String createRegistrationMessageContent(Author to, UUID stateToken) {
    return String.format(
        """
                    Hello %s,

                    Welcome to RDating! To authorize RDating to match you with other daters click the following link.
                    %s

                    Best regards,
                    The RDating Team""",
        to.username(), getAuthorizationUrl(stateToken));
  }

  private String getAuthorizationUrl(UUID stateToken) {
    return redditUrl
        + "/api/v1/authorize?client_id="
        + redditClientId
        + "%26response_type=code%26state="
        + stateToken.toString()
        + "%26redirect_uri="
        + redirectUrl
        + "%26duration=permanent%26scope=history";
  }

  public String getRegisteredMessageSubject() {
    return "Thank you for registering with RDating!";
  }

  public String createRegisteredMessageContent(Author to) {
    return String.format(
        """
                    Hello %s,

                    Thank you for registering with RDating! You can now start finding your matches at %s/r/%s.

                    Best regards,
                    The RDating Team""",
        to.username(), redditUrl, subreddit);
  }
}
