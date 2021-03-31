package pl.olszewski.Blog.service.Menagers;

import pl.olszewski.Blog.domain.Post;
import pl.olszewski.Blog.domain.PostAuthor;

import java.util.List;

public interface PostAuthorManager {

    List<PostAuthor> getAllPostAuthor();

    List<PostAuthor> findByPostId(Integer id);

    List<PostAuthor> findByAuthorId(Integer id);

    void deleteAllByPostId(Integer id);

    void addPostAuthor(PostAuthor postAuthor);

    void saveAll();
}
