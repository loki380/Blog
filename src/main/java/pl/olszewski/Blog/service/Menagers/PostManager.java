package pl.olszewski.Blog.service.Menagers;

import pl.olszewski.Blog.domain.Post;

import java.util.List;
import java.util.Optional;

public interface PostManager {

    Optional<Post> findById(Integer id);

    List<Post> getAllPosts();

    List<Post> getPostsByTag(String tag);

    void saveAll();

    void addPost(Post post);

    void removePost(Integer id);
}
