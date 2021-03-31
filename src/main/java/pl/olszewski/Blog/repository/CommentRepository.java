package pl.olszewski.Blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.olszewski.Blog.domain.Attachment;
import pl.olszewski.Blog.domain.Comment;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

    Optional<Comment> findById(int id);

    List<Comment> findAllByUsername(String username);

    List<Comment> findAllByIdPost(int id);

    void deleteById(Integer integer);

    void deleteAllByPostId(Integer id);

}
