package org.affinity.rdating.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MatchingController {

    @PostMapping("/match")
    public String match() {
        // Placeholder logic for matching
        return "Match successful!";
    }
}

