package pl.olszewski.Blog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import pl.olszewski.Blog.domain.Post;
import pl.olszewski.Blog.repository.PostRepository;
import pl.olszewski.Blog.service.Menagers.PostManager;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class PostInMemoryService implements PostManager {

    private PostRepository postRepository;
    private List<Post> posts;

    @Autowired
    public PostInMemoryService(PostRepository postRepository, List<Post> posts) {
        this.postRepository = postRepository;
        this.posts = posts;
    }

    @Override
    public void addPost(Post post) {
        postRepository.saveAndFlush(post);
    }

    @Override
    public Optional<Post> findById(Integer id) { return postRepository.findById(id); }

    @Override
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    @Override
    public List<Post> getPostsByTag(String tag) { return postRepository.findByTagsContaining(tag);}

    @Override
    public void removePost(Integer id) { postRepository.deleteById(id); }

    @Override
    public void saveAll() { postRepository.saveAll(posts); }

    @PostConstruct
    public void init() {
        saveAll();
    }
}
