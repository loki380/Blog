package pl.olszewski.Blog.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@Table(name = "posts")
@JsonPropertyOrder({"id","postContent","tags"})
public class Post {
    @Id
    @NonNull
    @Column(name = "post_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NonNull
    @Length(min = 1, message = "Content of the Post should be start at least one character")
    @Column(columnDefinition = "TEXT")
    private String post_content;

    @NonNull
    @Length(min = 1, message = "Tags should be start at least one character")
    private String tags;

    private String username;

    @OneToMany(mappedBy = "post", cascade = {CascadeType.MERGE})
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<PostAuthor> postsAuthors;

    @OneToMany(mappedBy = "post",cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<Comment> comments;

    @OneToMany(mappedBy = "post",cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<Attachment> attachments;
}
