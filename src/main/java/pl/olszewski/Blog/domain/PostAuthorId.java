package pl.olszewski.Blog.domain;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class PostAuthorId implements Serializable {

    @NonNull
    @Column(name = "id_post")
    @Getter @Setter
    private int id_post;

    @NonNull
    @Column(name = "id_author")
    @Getter @Setter
    private int id_author;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostAuthorId that = (PostAuthorId) o;
        return id_post == that.id_post &&
                id_author == that.id_author;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id_post, id_author);
    }
}
