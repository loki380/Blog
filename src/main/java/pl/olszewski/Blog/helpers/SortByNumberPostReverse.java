package pl.olszewski.Blog.helpers;

import pl.olszewski.Blog.domain.Post;

import java.util.Comparator;

public class SortByNumberPostReverse implements Comparator<Post> {
    @Override
    public int compare(Post post, Post t1) {
        return t1.getId()-post.getId();
    }
}
