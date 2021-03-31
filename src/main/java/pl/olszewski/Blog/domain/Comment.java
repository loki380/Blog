package pl.olszewski.Blog.domain;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@Table(name = "comments")
public class Comment {

    @Id
    @NonNull
    @Getter @Setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NonNull
    @Getter @Setter
    @Size(min = 2, message = "Username should be start at least two characters")
    private String username;

    @NonNull
    @Getter @Setter
    private int idPost;

    @NonNull
    @Getter @Setter
    @Size(min = 1, message = "Content of the comment should be start at least one character")
    @Column(columnDefinition = "TEXT")
    private String comment_content;

    @ManyToOne
    @Getter @Setter
    private Post post;
}
