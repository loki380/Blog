package pl.olszewski.Blog.service;

import org.hibernate.annotations.OrderBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.olszewski.Blog.domain.Author;
import pl.olszewski.Blog.domain.Post;
import pl.olszewski.Blog.domain.PostAuthor;
import pl.olszewski.Blog.domain.PostAuthorId;
import pl.olszewski.Blog.repository.AuthorRepository;
import pl.olszewski.Blog.repository.PostAuthorRepository;
import pl.olszewski.Blog.repository.PostRepository;
import pl.olszewski.Blog.service.Menagers.PostAuthorManager;

import javax.annotation.PostConstruct;
import javax.persistence.criteria.CriteriaBuilder;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class PostAuthorInMemoryService implements PostAuthorManager {


    private PostAuthorRepository postAuthorRepository;
    private AuthorRepository authorRepository;
    private PostRepository postRepository;
    private List<PostAuthorId> postAuthors;

    @Autowired
    public PostAuthorInMemoryService(PostAuthorRepository postAuthorRepository,
                                     AuthorRepository authorRepository,
                                     PostRepository postRepository,
                                     List<PostAuthorId> postsAuthors,
                                     AuthorInMemoryService authorInMemoryService) {
        this.postAuthorRepository = postAuthorRepository;
        this.authorRepository = authorRepository;
        this.postRepository = postRepository;
        this.postAuthors = postsAuthors;
    }

    @Override
    public void addPostAuthor(PostAuthor postAuthor) {
        postAuthorRepository.save(postAuthor);
    }

    @Override
    public List<PostAuthor> getAllPostAuthor() { return postAuthorRepository.findAll(); }

    @Override
    public List<PostAuthor> findByPostId(Integer id) {
        List<PostAuthor> result = new ArrayList<>();
        for (PostAuthor postAuthor : postAuthorRepository.findAll()) {
            if (postAuthor.getPost().getId() == id) {
                result.add(postAuthor);
            }
        }
        return result;
    }

    @Override
    public List<PostAuthor> findByAuthorId(Integer id) {
        List<PostAuthor> result = new ArrayList<>();
        for (PostAuthor postAuthor : postAuthorRepository.findAll()) {
            if (postAuthor.getAuthor().getId() == id) {
                result.add(postAuthor);
            }
        }
        return result;
    }

    @Override
    public void deleteAllByPostId(Integer id){
        postAuthorRepository.deleteAllByPostId(id);
    }

    @Override
    public void saveAll() {
        // Save postAuthors from bean
        for (PostAuthorId p : postAuthors) {
            postAuthorRepository.save(new PostAuthor(p, postRepository.findById(p.getId_post()).get(),authorRepository.findById(p.getId_author()).get()));
        }
        // Set Author to PostAuthor
        for (Post p : postRepository.findAll()) {
            if(postAuthorRepository.findAllByPost(p).size()!=0) {
                p.setPostsAuthors(postAuthorRepository.findAllByPost(p));
            }
        }

        // Set Post to PostAuthor
        for (Author a : authorRepository.findAll()) {
            if(postAuthorRepository.findAllByAuthor(a).size()!=0) {
                a.setPostsAuthors(postAuthorRepository.findAllByAuthor(a));
            }
        }
    }
    @PostConstruct
    public void init() {
        saveAll();
    }
}
