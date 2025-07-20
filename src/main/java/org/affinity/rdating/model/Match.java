package org.affinity.rdating.model;

import java.util.Objects;

public class Match {

    private final Post post1;
    private final Post post2;

    public Match(Post post1, Post post2) {
        this.post1 = post1;
        this.post2 = post2;
    }

    public Post getPost1() {
        return post1;
    }

    public Post getPost2() {
        return post2;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Match match = (Match) o;
        return Objects.equals(post1.getAuthor(), match.post1.getAuthor())
                && Objects.equals(post2.getAuthor(), match.post2.getAuthor());
    }

    @Override
    public int hashCode() {
        return Objects.hash(post1.getAuthor(), post2.getAuthor());
    }
}
