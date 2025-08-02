/* (C)2025 */
package org.affinity.rdating.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import org.affinity.rdating.service.MatchingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Matching", description = "APIs for matching users in subreddit")
@RestController
public class MatchingController {

  private final MatchingService matchingService;

  @Value("${reddit.subreddit.name}")
  private String subreddit;

  public MatchingController(MatchingService matchingService) {
    this.matchingService = matchingService;
  }

  @Operation(
      summary = "Run matching algorithm",
      description = "Triggers the matching process for the configured subreddit.")
  @PostMapping("/match")
  public void match() throws IOException, InterruptedException {
    matchingService.match(subreddit);
  }

  @Operation(summary = "Update comments", description = "Updates like comments with images.")
  @PostMapping("/updateComments")
  public void updateComments() throws IOException, InterruptedException {
    matchingService.match(subreddit);
  }
}
