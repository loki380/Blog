package pl.olszewski.Blog.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "posts_authors")
public class PostAuthor {

    @EmbeddedId
    @Getter
    @Setter
    private PostAuthorId id;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @MapsId("id_post")
    @JoinColumn(name = "id_post")
    @Getter @Setter
    @NonNull
    Post post;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @MapsId("id_author")
    @JoinColumn(name = "id_author")
    @Getter @Setter
    @NonNull
    Author author;
}
