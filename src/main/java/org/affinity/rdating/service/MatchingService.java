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

    private final RedditClient redditClient;
    private final PostService postService;
    private final MatchRepository matchRepository;
    private final MessageContentService messageContentService;
    private final CommentService commentService;

    public MatchingService(RedditClient redditClient, PostService postService, MatchRepository matchRepository,
                           MessageContentService messageContentService, CommentService commentService) {
        this.redditClient = redditClient;
        this.postService = postService;
        this.matchRepository = matchRepository;
        this.messageContentService = messageContentService;
        this.commentService = commentService;
    }

    public void match(String subreddit) throws IOException, InterruptedException {
        List<Post> posts = postService.getPosts(subreddit);
        logger.info("Fetched {} posts from subreddit: {}", posts.size(), subreddit);
        Map<Author,List<Post>> authorToPosts =
                posts.stream().collect(Collectors.groupingBy(Post::getAuthor));
        Graph<Post> graph = buildGraph(subreddit, posts, authorToPosts);
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

    private Graph<Post> buildGraph(
            String subreddit,
            List<Post> posts,
            Map<Author,List<Post>> authorToPosts) throws IOException, InterruptedException {
        Graph<Post> graph = new Graph<>();
        for (Post post : posts) {
            addEdgesFromComments(subreddit, post, authorToPosts, graph);
        }
        return graph;
    }

    private void addEdgesFromComments(
            String subreddit,
            Post post,
            Map<Author,List<Post>> authorToPosts,
            Graph<Post> graph) throws IOException, InterruptedException {
        List<Comment> comments = commentService.getComments(subreddit, post);
        logger.info("Fetched %s comments for post: %s", comments.size(), post.getId());
        for(Comment comment : comments) {
            List<Post> posts = authorToPosts.get(comment.author());
            if(posts != null){
                graph.addEdge(post, posts.getFirst());
            }
        }
    }

}
