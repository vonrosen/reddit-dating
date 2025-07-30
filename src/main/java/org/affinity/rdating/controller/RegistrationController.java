package org.affinity.rdating.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.affinity.rdating.service.RegistrationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Tag(name = "Registration", description = "APIs for registering users in subreddit")
@RestController
public class RegistrationController {

    private final RegistrationService registrationService;

    @Value("${reddit.subreddit.name}")
    private String subreddit;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @Operation(summary = "Add users", description = "Add new users.")
    @PostMapping("/addUsers")
    public void addUsers() throws IOException, InterruptedException {
        registrationService.addUsers(subreddit);
    }

    @Operation(summary = "Send registration message", description = "Sends registration message to new users.")
    @PostMapping("/sendRegistrationMessages")
    public void sendRegistrationMessages() throws IOException, InterruptedException {
        registrationService.sendRegistrationMessages(subreddit);
    }

}
