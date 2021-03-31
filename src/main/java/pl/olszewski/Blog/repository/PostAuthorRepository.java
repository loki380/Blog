package pl.olszewski.Blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.olszewski.Blog.domain.Author;
import pl.olszewski.Blog.domain.Post;
import pl.olszewski.Blog.domain.PostAuthor;
import pl.olszewski.Blog.domain.PostAuthorId;

import java.util.List;

@Repository
public interface PostAuthorRepository extends JpaRepository<PostAuthor, PostAuthorId> {
    List<PostAuthor> findAllByAuthor(Author author);

    List<PostAuthor> findAllByPost(Post post);

    void deleteAllByPostId(Integer id);
}
