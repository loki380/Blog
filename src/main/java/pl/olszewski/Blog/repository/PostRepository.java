package pl.olszewski.Blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.olszewski.Blog.domain.Post;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
    Optional<Post> findById (int id);
    List<Post> findByTagsContaining(String tag);
    List<Post> findByTagsContains(String tag);
    void deleteById(Integer integer);
}
