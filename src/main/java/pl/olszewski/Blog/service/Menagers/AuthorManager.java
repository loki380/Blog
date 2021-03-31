package pl.olszewski.Blog.service.Menagers;

import org.springframework.security.core.userdetails.UserDetailsService;
import pl.olszewski.Blog.domain.Author;
import pl.olszewski.Blog.dto.UserRegistrationDto;

import java.util.List;
import java.util.Optional;

public interface AuthorManager extends UserDetailsService {

    Optional<Author> findById(Integer id);

    Author findByUsername(String username);

    List<Author> getAllAuthors();

    Author save(UserRegistrationDto userRegistrationDto);

    Author save(UserRegistrationDto userRegistrationDto, String role);

    boolean existsByUsername(String username);

    void saveAll();

    void addAuthor(Author author);

    void removeAuthor(Integer id);


}
