package org.affinity.rdating.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MessageContentService {

    @Value("${reddit.baseUrl}")
    private String redditUrl;


    public String getMessageSubject(){
        return "New Match Notification from RDating";
    }

    public String createMatchMessageContent(String matchUsername, String matchPermalink) {
        return String.format("You have a new match with %s! Check out their profile here: %s%s",
                matchUsername, redditUrl, matchPermalink);
    }
}
