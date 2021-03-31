package pl.olszewski.Blog.helpers;

import pl.olszewski.Blog.domain.Post;

import java.util.Comparator;

public class SortByContent implements Comparator<Post> {
    @Override
    public int compare(Post post, Post t1) {
        return post.getPost_content().compareTo(t1.getPost_content());
    }
}
