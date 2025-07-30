package org.affinity.rdating.service;

import org.affinity.rdating.entity.UserEntity;
import org.affinity.rdating.entity.UserRepository;
import org.affinity.rdating.model.Author;
import org.affinity.rdating.model.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class RegistrationService {
    private static final Logger logger = LoggerFactory.getLogger(RegistrationService.class);

    private final PostService postService;
    private final UserRepository userRepository;

    public RegistrationService(PostService postService, UserRepository userRepository) {
        this.postService = postService;
        this.userRepository = userRepository;
    }

    public void addUsers(String subreddit)  throws IOException, InterruptedException {
        List<Post> posts = postService.getPosts(subreddit);
        logger.info("Fetched {} posts from subreddit: {}", posts.size(), subreddit);
        Map<Author, UserEntity> authorToUser = userRepository.findAll().stream()
                .collect(Collectors.toMap(
                        userEntity -> new Author(userEntity.getUserName()),
                        Function.identity()));
        logger.info("Fetched {} users", authorToUser.size());
        posts.stream()
                .filter(post -> !authorToUser.containsKey(post.getAuthor()))
                .forEach(post -> {
                    UserEntity userEntity = new UserEntity(post.getAuthor().username());
                    userRepository.save(userEntity);
                    logger.info("Added new user: {}", post.getAuthor().username());
                });
    }

    public void sendRegistrationMessages(String subreddit) throws IOException, InterruptedException {
    }
}
