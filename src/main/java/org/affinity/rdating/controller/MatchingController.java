package org.affinity.rdating.controller;

import org.affinity.rdating.service.MatchingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MatchingController {

    private final MatchingService matchingService;

    @Value("${reddit.subreddit.name}")
    private String subreddit;

    public MatchingController(MatchingService matchingService) {
        this.matchingService = matchingService;
    }

    @PostMapping("/match")
    public void match() {
        matchingService.match(subreddit);
    }
}

