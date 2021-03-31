package pl.olszewski.Blog.service.Menagers;

import org.springframework.web.multipart.MultipartFile;
import pl.olszewski.Blog.domain.Attachment;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;
import java.util.Map;

public interface AttachmentManager {

    Attachment findByFileName(String filename);

    List<Attachment> getAllAttachments();

    List<Attachment> getAllByPost(int id_post);

    void deleteAllByPostId(Integer id);

    void deleteById(Integer id);

    void saveAll();

    void add(MultipartFile[] files, int id_post);

    void remove(String filename);
}
