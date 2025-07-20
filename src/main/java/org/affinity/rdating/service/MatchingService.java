package org.affinity.rdating.service;

import org.affinity.rdating.client.RedditClient;
import org.affinity.rdating.entity.MatchEntity;
import org.affinity.rdating.entity.MatchRepository;
import org.affinity.rdating.model.Author;
import org.affinity.rdating.model.Comment;
import org.affinity.rdating.model.Match;
import org.affinity.rdating.model.Post;
import org.affinity.rdating.util.Graph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MatchingService {
    private static final Logger logger = LoggerFactory.getLogger(MatchingService.class);

    private static final int POST_LIMIT = 1000;
    private static final int COMMENT_LIMIT = 1000;

    private final RedditClient redditClient;
    private final MatchRepository matchRepository;
    private final MessageContentService messageContentService;

    public MatchingService(RedditClient redditClient, MatchRepository matchRepository,
                           MessageContentService messageContentService) {
        this.redditClient = redditClient;
        this.matchRepository = matchRepository;
        this.messageContentService = messageContentService;
    }

    public void match(String subreddit) throws IOException, InterruptedException {
        List<Post> posts = getPosts(subreddit);
        logger.info("Fetched {} posts from subreddit: {}", posts.size(), subreddit);
        Map<Author,List<Post>> authorToPosts =
                posts.stream().collect(Collectors.groupingBy(Post::getAuthor));
        Graph<Post> graph = buildGraph(subreddit, authorToPosts);
        Set<Match> matches = findMatches(graph);
        removeExistingMatches(matches);
        logger.info("Found {} matches in subreddit: {}", matches.size(), subreddit);
        sendMatchMessages(subreddit, matches);
    }

    private void sendMatchMessages(String subreddit, Set<Match> matches) throws IOException, InterruptedException {
        for(Match match : matches) {
            matchRepository.save(
                    new MatchEntity(
                            match.getPost1().getAuthor().username(),
                            match.getPost2().getAuthor().username(),
                            match.getPost1().getPermaLink(),
                            match.getPost2().getPermaLink(),
                            Instant.now()));
            redditClient.sendMessage(
                    subreddit,
                    match.getPost1().getAuthor().username(),
                    messageContentService.getMessageSubject(),
                    messageContentService.createMatchMessageContent(
                            match.getPost2().getAuthor().username(),
                            match.getPost2().getPermaLink()));
            redditClient.sendMessage(subreddit,
                    match.getPost2().getAuthor().username(),
                    messageContentService.getMessageSubject(),
                    messageContentService.createMatchMessageContent(
                            match.getPost1().getAuthor().username(),
                            match.getPost1().getPermaLink()));
            logger.info("New match found: {} and {}", match.getPost1().getAuthor(), match.getPost2().getAuthor());
        }
    }

    private void removeExistingMatches(Set<Match> existingMatches) {
        matchRepository.findAll().forEach(matchEntity ->
                existingMatches.remove(new Match(
                        new Post("", "", "", new Author(matchEntity.getUser1())),
                        new Post("", "", "", new Author(matchEntity.getUser2()))))
        );
    }

    private Set<Match> findMatches(Graph<Post> graph) {
        Set<Match> matches = new HashSet<>();
        for (Post post : graph.vertices()) {
            Set<Post> postMatches = match(post, graph);
            postMatches.remove(post);
            for(Post match : postMatches){
                List<Post> sorted = new ArrayList<>(Arrays.asList(post, match));
                sorted = sorted.stream().sorted().toList();
                matches.add(new Match(sorted.getFirst(), sorted.getLast()));
            }
        }
        return matches;
    }

    private Set<Post> match(
            Post post,
            Graph<Post> graph) {
        Set<Post> matches = new HashSet<>();
        for(Post edge : graph.edges(post)) {
            Set<Author> authors = graph.edges(edge)
                    .stream()
                    .map(Post::getAuthor)
                    .collect(Collectors.toSet());

            if(authors.contains(post.getAuthor())){
                matches.add(edge);
            }
        }
        return matches;
    }

    private List<Post> getPosts(String subreddit) throws IOException, InterruptedException {
        List<Post> posts = new ArrayList<>();
        String after = null;
        do {
            List<Post> fetchedPosts = redditClient.getPosts(subreddit, after, POST_LIMIT);
            posts.addAll(fetchedPosts);
            after = fetchedPosts.getLast().getId();
        } while(posts.size() == POST_LIMIT);
        return posts;
    }

    private Graph<Post> buildGraph(String subreddit, Map<Author,List<Post>> authorToPosts) throws IOException, InterruptedException {
        Graph<Post> graph = new Graph<>();
        String after = null;
        List<Post> posts;
        do {
            posts = redditClient.getPosts(subreddit, after, POST_LIMIT);
            if(posts.isEmpty()){
                return graph;
            }
            for (Post post : posts) {
                addEdgesFromComments(subreddit, post, authorToPosts, graph);
            }
            after = posts.getLast().getId();

        } while(posts.size() == POST_LIMIT);
        return graph;
    }

    private void addEdgesFromComments(
            String subreddit,
            Post post,
            Map<Author,List<Post>> authorToPosts,
            Graph<Post> graph) throws IOException, InterruptedException {
        List<Comment> comments;
        String after = null;
        do {
            comments = redditClient.getComments(subreddit, post.getId(), after, COMMENT_LIMIT);
            if(comments.isEmpty()) {
                return;
            }
            for(Comment comment : comments){
                List<Post> posts = authorToPosts.get(comment.author());
                if(posts != null){
                    graph.addEdge(post, posts.getFirst());
                }
            }
            after = comments.getLast().id();
        } while(comments.size() == POST_LIMIT);
    }

//    private void addAuthors(
//            String subreddit,
//            RedditPostListing.Child post,
//            List<RedditCommentArray.Listing> comments,
//            Graph<String> graph) {
//        for (RedditPostListing.Child child : posts.data.children) {
//            List<RedditCommentArray.Listing> comments = null;
//            String after = null;
//            do {
//                try {
//                    comments = redditClient.getComments(subreddit, child.data.id, after, COMMENT_LIMIT);
//                    if(comments.isEmpty()) {
//                        return;
//                    }
//                    addAuthors(subreddit, child, comments, graph);
//                    after = posts.data.children.getLast().data.id;
//                }catch (Exception e) {
//                    logger.error("Error fetching posts from subreddit: " + subreddit, e);
//                }
//            } while(posts.data.children.size() == POST_LIMIT);
//
//            try {
//                List<RedditCommentArray.Listing> comments = redditClient.getComments(subreddit, child.data.id, COMMENT_LIMIT);
//                comments.forEach(
//                        listing -> {
//                            listing.data.children.forEach(
//                                    child -> {
//                                        if (child.kind.equals("t1")) { // Ensure it's a comment
//                                            JsonObject commentData = child.data;
//                                            logger.info("Comment by: " + commentData.get("author") + " on post: " + post.data.id);
//                                        }
//                                    }
//                            );
//                        }
//                );
//            }catch (Exception e) {
//                logger.error("Error fetching comments for post: " + post.data.id, e);
//            }
//        }
//    }
}
