package pl.olszewski.Blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.olszewski.Blog.domain.Author;

import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Integer> {

    Optional<Author> findById(int id);

    Author findByUsername(String username);

    boolean existsByUsername(String username);

    void deleteById(Integer integer);
}
