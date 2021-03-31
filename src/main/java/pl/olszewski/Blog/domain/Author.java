package pl.olszewski.Blog.domain;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@Getter @Setter
@Table(name = "authors")
public class Author {

    public Author(String first_name,
                  String last_name,
                  String username,
                  String email,
                  String password,
                  Collection<Role> roles) {

        this.first_name = first_name;
        this.last_name = last_name;
        this.username = username;
        this.email = email;
        this.password = password;
        this.roles = roles;
    }

    @NonNull
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NonNull
    @Size(min = 2, message = "First Name should be start at least two characters")
    private String first_name;

    @NonNull
    @Size(min = 2, message = "Last Name should be start at least two characters")
    private String last_name;

    @NonNull
    @Size(min = 2, message = "Username should be start at least two characters")
    private String username;

    @Email
    private String email;

    private String password;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private Collection<Role> roles;

    @OneToMany(mappedBy = "author", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private List<PostAuthor> postsAuthors;
}
