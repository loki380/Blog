package pl.olszewski.Blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.olszewski.Blog.domain.Attachment;
import pl.olszewski.Blog.domain.Author;

import java.util.List;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Integer> {

    Attachment findById(int id);

    Attachment findByFilename(String filename);

    List<Attachment> findAllByIdPost(int id);

    void deleteByFilename(String filename);

    void deleteAllByPostId(Integer id);

}
