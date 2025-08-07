/*
  Copyright 2025, RDating. All rights reserved.
*/
package org.affinity.rdating.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import org.affinity.rdating.service.ModerationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Moderation", description = "APIs for moderating posts in subreddit")
@RestController
public class ModerationController {

  @Value("${reddit.subreddit.name}")
  private String subreddit;

  private final ModerationService moderationService;

  public ModerationController(ModerationService moderationService) {
    this.moderationService = moderationService;
  }

  @Operation(summary = "Remove nsfw posts", description = "Removes posts marked as over 18.")
  @PostMapping("/removeNsfw")
  public void removeNsfw() throws IOException, InterruptedException {
    moderationService.removeNsfw(subreddit);
  }

  @Operation(summary = "Remove duplicate posts", description = "Removes duplicate posts.")
  @PostMapping("/removeDuplicates")
  public void removeDuplicates() throws IOException, InterruptedException {
    moderationService.removeDuplicates(subreddit);
  }
}
