package org.affinity.rdating.service;

import com.google.gson.JsonObject;
import org.affinity.rdating.client.RedditClient;
import org.affinity.rdating.client.RedditCommentArray;
import org.affinity.rdating.client.RedditPostListing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MatchingService {
    private static final Logger logger = LoggerFactory.getLogger(MatchingService.class);

    private static final int POST_LIMIT = 1000;
    private static final int COMMENT_LIMIT = 1000;

    private final RedditClient redditClient;

    public MatchingService(RedditClient redditClient) {
        this.redditClient = redditClient;
    }

    public void match(String subreddit) {
        RedditPostListing posts = null;
        try {
            posts = redditClient.getPosts(subreddit, POST_LIMIT);
        }catch (Exception e) {
            logger.error("Error fetching posts from subreddit: " + subreddit, e);
        }
        posts.data.children.forEach(
            post -> {
                logger.info("Processing post with author: " + post.data.author);
                try {
                    List<RedditCommentArray.Listing> comments = redditClient.getComments(subreddit, post.data.id, COMMENT_LIMIT);
                    comments.forEach(
                            listing -> {
                                listing.data.children.forEach(
                                    child -> {
                                        if (child.kind.equals("t1")) { // Ensure it's a comment
                                            JsonObject commentData = child.data;
                                            logger.info("Comment by: " + commentData.get("author") + " on post: " + post.data.id);
                                        }
                                    }
                                );
                            }
                    );
                }catch (Exception e) {
                    logger.error("Error fetching comments for post: " + post.data.id, e);
                }
            }
        );
    }
}
