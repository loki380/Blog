package pl.olszewski.Blog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pl.olszewski.Blog.domain.Attachment;
import pl.olszewski.Blog.domain.Author;
import pl.olszewski.Blog.domain.Post;
import pl.olszewski.Blog.repository.AttachmentRepository;
import pl.olszewski.Blog.repository.AuthorRepository;
import pl.olszewski.Blog.repository.PostRepository;
import pl.olszewski.Blog.service.Menagers.AttachmentManager;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class AttachmentMemoryService implements AttachmentManager {

    AttachmentRepository attachmentsRepository;
    PostRepository postRepository;
    private List<Attachment> attachments;

    @Autowired
    public AttachmentMemoryService(AttachmentRepository attachmentRepository,
                                   PostRepository postRepository,
                                   List<Attachment> attachments){

        this.attachmentsRepository=attachmentRepository;
        this.postRepository=postRepository;
        this.attachments=attachments;
    }
    @Override
    public void add(MultipartFile[] files, int idPost) {
        for (MultipartFile file : files) {
            if (file.getOriginalFilename() != null && !file.getOriginalFilename().equals("")) {
                Attachment attachment = new Attachment();
                attachment.setIdPost(idPost);
                attachment.setFilename(file.getOriginalFilename());
                attachmentsRepository.save(attachment);
            }
        }
    }

    @Override
    public Attachment findByFileName(String filename) { return attachmentsRepository.findByFilename(filename);}

    @Override
    public List<Attachment> getAllAttachments() { return attachmentsRepository.findAll();}

    @Override
    public List<Attachment> getAllByPost(int idPost) { return attachmentsRepository.findAllByIdPost(idPost);}

    @Override
    public void remove(String filename) { attachmentsRepository.deleteByFilename(filename);}

    @Override
    public void deleteById(Integer id) { attachmentsRepository.deleteById(id);}

    @Override
    public void saveAll() {
        attachmentsRepository.saveAll(attachments);
        for (Post p : postRepository.findAll()) {
            if(attachmentsRepository.findAllByIdPost(p.getId()).size()!=0) {
                Post post = p;
                post.setAttachments(attachmentsRepository.findAllByIdPost(p.getId()));
                postRepository.save(post);
                for (Attachment a : attachmentsRepository.findAllByIdPost(p.getId())) {
                    Attachment attachment = a;
                    attachment.setPost(postRepository.findById(a.getIdPost()).get());
                    attachmentsRepository.save(attachment);
                }
            }
        }
    }
    public void deleteAllByPostId(Integer id){
        attachmentsRepository.deleteAllByPostId(id);
    }
    @PostConstruct
    public void init() {
        saveAll();
    }
}
