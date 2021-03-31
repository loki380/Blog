package pl.olszewski.Blog.service.Menagers;

import pl.olszewski.Blog.domain.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentManager {

    Optional<Comment> findById(Integer id);

    List<Comment> findByPostId(Integer id);

    List<Comment> getAllComment();

    List<Comment> findByUsername(String user);

    void deleteAllByPostId(Integer id);

    void saveAll();

    void addComment(Comment comment);

    void removeComment(Integer id);
}
