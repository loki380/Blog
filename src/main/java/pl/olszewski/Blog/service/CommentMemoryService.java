package pl.olszewski.Blog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.olszewski.Blog.domain.Comment;
import pl.olszewski.Blog.domain.Post;
import pl.olszewski.Blog.repository.CommentRepository;
import pl.olszewski.Blog.repository.PostRepository;
import pl.olszewski.Blog.service.Menagers.CommentManager;

import javax.annotation.PostConstruct;
import javax.persistence.criteria.CriteriaBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CommentMemoryService implements CommentManager {

    private CommentRepository commentRepository;
    private PostRepository postRepository;
    private List<Comment> comments;

    @Autowired
    public CommentMemoryService(CommentRepository commentRepository,
                                PostRepository postRepository,
                                List<Comment> comments) {

        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.comments = comments;
    }

    @Override
    public void addComment(Comment comment) { commentRepository.save(comment); }

    @Override
    public Optional<Comment> findById(Integer id) { return commentRepository.findById(id); }

    @Override
    public List<Comment> findByPostId(Integer id) { return commentRepository.findAllByIdPost(id);}

    @Override
    public List<Comment> getAllComment() {
        return commentRepository.findAll();
    }

    @Override
    public List<Comment> findByUsername(String user) { return commentRepository.findAllByUsername(user); }

    @Override
    public void removeComment(Integer id) { commentRepository.deleteById(id);}

    @Override
    public void saveAll() {
        commentRepository.saveAll(comments);
        for (Post p : postRepository.findAll()) {
            if(commentRepository.findAllByIdPost(p.getId()).size()!=0) {
                Post post = p;
                post.setComments(commentRepository.findAllByIdPost(p.getId()));
                postRepository.save(post);
                for (Comment c : commentRepository.findAllByIdPost(p.getId())) {
                    Comment comment = c;
                    comment.setPost(postRepository.findById(c.getIdPost()).get());
                    commentRepository.save(comment);
                }
            }
        }
    }
    public void deleteAllByPostId(Integer id){
        commentRepository.deleteAllByPostId(id);
    }

    @PostConstruct
    public void init() {
        saveAll();
    }
}
