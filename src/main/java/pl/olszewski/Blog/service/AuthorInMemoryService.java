package pl.olszewski.Blog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import pl.olszewski.Blog.domain.Author;
import pl.olszewski.Blog.domain.Role;
import pl.olszewski.Blog.dto.UserRegistrationDto;
import pl.olszewski.Blog.repository.AuthorRepository;
import pl.olszewski.Blog.service.Menagers.AuthorManager;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AuthorInMemoryService implements AuthorManager {

    AuthorRepository authorRepository;
    private List<Author> authors;
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public AuthorInMemoryService(AuthorRepository authorRepository, List<Author> authors, BCryptPasswordEncoder passwordEncoder){
        this.authorRepository=authorRepository;
        this.authors=authors;
        this.passwordEncoder=passwordEncoder;
    }

    @Override
    public void addAuthor(Author author) {
        authorRepository.save(author);
    }

    @Override
    public Optional<Author> findById(Integer id) { return authorRepository.findById(id);}

    @Override
    public List<Author> getAllAuthors() { return authorRepository.findAll(); }

    @Override
    public void removeAuthor(Integer id) { authorRepository.deleteById(id);}

    @Override
    public void saveAll() { authorRepository.saveAll(authors); }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Author author = authorRepository.findByUsername(s);
        if(author==null) {
            throw new UsernameNotFoundException("Invalid username or password");
        }
        return new User(author.getUsername(), author.getPassword(), mapRolesToAuthorities(author.getRoles()));
    }
    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
    }

    @Override
    public Author save(UserRegistrationDto userDto) {
        Author user = new Author(userDto.getFirstName(),
                userDto.getLastName(), userDto.getUsername(),
                userDto.getEmail(),
                passwordEncoder.encode(userDto.getPassword()),
                Arrays.asList(new Role("ROLE_USER")));
        return authorRepository.save(user);
    }

    @Override
    public Author save(UserRegistrationDto userDto, String role) {
        Author user = new Author(userDto.getFirstName(),
                userDto.getLastName(), userDto.getUsername(),
                userDto.getEmail(),
                passwordEncoder.encode(userDto.getPassword()),
                Arrays.asList(new Role(role)));
        return authorRepository.save(user);
    }

    @Override
    public boolean existsByUsername(String username) { return authorRepository.existsByUsername(username); }

    @Override
    public Author findByUsername(String username) { return authorRepository.findByUsername(username); }

    @PostConstruct
    public void init() {
        saveAll();
        save(new UserRegistrationDto("admin", "admin", "admin", "admin@admin.pl", "admin"), "ROLE_ADMIN");
        save(new UserRegistrationDto("user", "user", "user", "user@user.pl", "user"), "ROLE_USER");
    }
}
