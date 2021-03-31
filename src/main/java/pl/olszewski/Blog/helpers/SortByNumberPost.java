package pl.olszewski.Blog.helpers;

import pl.olszewski.Blog.domain.Post;

import java.util.Comparator;

public class SortByNumberPost implements Comparator<Post> {
    @Override
    public int compare(Post post, Post t1) {
        return post.getId()-t1.getId();
    }
}
