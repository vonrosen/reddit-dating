/* (C)2025 */
package org.affinity.rdating.service;

import java.util.UUID;
import org.affinity.rdating.model.Author;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MessageContentService {

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
        "Hello %s,\n\nWelcome to RDating! To authorize RDating to match you with other daters click the following link.\n"
            + "%s\n"
            + "Best regards,\nThe RDating Team",
        to.username(), getAuthorizationUrl(stateToken));
  }

  private String getAuthorizationUrl(UUID stateToken) {
    return String.format(
        "%s/api/v1/authorize?client_id=%s&response_type=code&state=%s&redirect_uri=%s",
        redditUrl, redditClientId, stateToken.toString(), redirectUrl);
  }
}
