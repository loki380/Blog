package pl.olszewski.Blog.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.olszewski.Blog.domain.*;
import pl.olszewski.Blog.dto.UserRegistrationDto;
import pl.olszewski.Blog.helpers.*;
import pl.olszewski.Blog.service.Menagers.*;
import pl.olszewski.Blog.storage.StorageService;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

@RestController
public class PostController {
    private PostManager pm;
    private PostAuthorManager pam;
    private AuthorManager am;
    private CommentManager cm;
    private AttachmentManager atm;
    private final StorageService storageService;

    protected final Logger log = Logger.getLogger(getClass().getName());

    public PostController(PostManager pm,
                          PostAuthorManager pam,
                          AuthorManager am,
                          CommentManager cm,
                          AttachmentManager atm,
                          StorageService storageService) {
        this.pm = pm;
        this.pam = pam;
        this.am=am;
        this.cm=cm;
        this.atm=atm;
        this.storageService=storageService;
    }

    @GetMapping("/api/posts")
    public List<Post> getAllPosts() { return pm.getAllPosts(); }

    @GetMapping("/api/post/{id}")
    public Post getPost(@PathVariable("id") int id) { return pm.findById(id).get(); }

    @GetMapping("/api/author_posts/{id}")
    public List<Post> getAllPostsAuthor(@PathVariable("id") int id) {
        List<Post> result = new ArrayList<>();
        for(PostAuthor postAuthor : pam.findByAuthorId(id)){
            result.add(pm.findById(postAuthor.getPost().getId()).get());
        }
        return result;
    }

    @PostMapping("/api/register")
    public UserRegistrationDto registerUser(@RequestBody UserRegistrationDto registrationDto){
        am.save(registrationDto);
        return registrationDto;
    }

    @PostMapping("/api/newpost")
    public String newPost(@RequestBody Post post) {

        post.setTags(post.getTags().replace(","," "));
        if(am.findByUsername(post.getUsername()) == null) {
            return "This user not exist";
        }
        pm.addPost(post);
        PostAuthorId pa = new PostAuthorId(post.getId(),am.findByUsername(post.getUsername()).getId());
        pam.addPostAuthor(new PostAuthor(pa,post,am.findByUsername(post.getUsername())));
        return "Success";
    }

    @PostMapping("/api/addTags/{id}")
    public String newPost(@RequestBody String tags, @PathVariable("id") int id) {
        Post post = pm.findById(id).get();
        post.setTags(post.getTags()+" "+tags);
        pm.addPost(post);
        return "Success";
    }

    @DeleteMapping("api/post/{id}")
    public String deletePost(@PathVariable("id") int id) {
        atm.deleteAllByPostId(id);
        cm.deleteAllByPostId(id);
        pam.deleteAllByPostId(id);
        pm.removePost(id);
        return "Success";
    }

    @PutMapping("api/post/{id}")
    public String updatePost(@RequestBody Post post, @PathVariable("id") int id) {
        Post postToEdit=pm.findById(id).get();
        postToEdit.setPost_content(post.getPost_content());
        pm.addPost(postToEdit);
        return "Success";
    }
    @GetMapping("api/posts/{sort}")
    public List<Post> sortBy(@PathVariable("sort") String sort) {
        List<Post> posts = new ArrayList<>(pm.getAllPosts());
        switch(sort)
        {
            case "number":
                Collections.sort(posts, new SortByNumberPost());
                return posts;
            case "number_reverse":
                Collections.sort(posts, new SortByNumberPostReverse());
                return posts;
            case "author":
                Collections.sort(posts, new SortByAuthor(am,pam));
                return posts;
            case "author_reverse":
                Collections.sort(posts, new SortByAuthorReverse(am,pam));
                return posts;
            case "content":
                Collections.sort(posts, new SortByContent());
                return posts;
            case "content_reverse":
                Collections.sort(posts, new SortByContentReverse());
                return posts;
            default:
                return posts;
        }
    }
    @PostMapping("api/comment")
    public String addComment(@RequestBody Comment comment) {
        cm.addComment(comment);
        return "Success";
    }
    @PutMapping("api/comment/{id}")
    public String updateComment(@RequestBody Comment comment, @PathVariable("id") int id) {
        comment.setId(id);
        comment.setIdPost(cm.findById(id).get().getIdPost());
        if(comment.getUsername()==null){
            comment.setUsername(cm.findById(id).get().getUsername());
        }
        cm.addComment(comment);
        return "Success";
    }
    @DeleteMapping("api/comment/{id}")
    public String deleteComment(@PathVariable("id") int id) {
        cm.removeComment(cm.findById(id).get().getId());
        return "Success";
    }
    @GetMapping("api/user/posts/{id}")
    public List<Post> authorPosts(@PathVariable("id") int id) {
        List<Post> posts = new ArrayList<>();
        for(PostAuthor postAuthor: pam.getAllPostAuthor()){
            if(postAuthor.getAuthor().getId()==id){
                posts.add(pm.findById(postAuthor.getPost().getId()).get());
            }
        }
        return posts;
    }
    @GetMapping("api/user/comments/{username}")
    public List<Comment> authorComments(@PathVariable("username") String username) {
        List<Comment> comments = new ArrayList<>();
        for(Comment comment: cm.getAllComment()){
            if(comment.getUsername().equals(username)){
                comments.add(comment);
            }
        }
        return comments;
    }
    @GetMapping("api/posts/{username}/{tag}")
    public List<Post> PostsByUsernameAndTag(@PathVariable("username") String username, @PathVariable("tag") String tag) {
        List<Post> posts = new ArrayList<>();
        for(PostAuthor postAuthor: pam.getAllPostAuthor()){
            if(postAuthor.getAuthor().getUsername().equals(username)){
                if(postAuthor.getPost().getTags().contains(tag)){
                    posts.add(postAuthor.getPost());
                }
            }
        }
        return posts;
    }
    @GetMapping("api/comments/{username}/{content}")
    public List<Comment> CommentByUsernameAndContent(@PathVariable("username") String username, @PathVariable("content") String content) {
        List<Comment> comments = new ArrayList<>();
        for(Comment comment: cm.getAllComment()){
            if(comment.getUsername().equals(username) && comment.getComment_content().contains(content)){
                comments.add(comment);
            }
        }
        return comments;
    }
    @DeleteMapping ("api/attachment/{id}")
    public String deleteAttachment(@PathVariable("id") int id) {
        atm.deleteById(id);
        return "Success";
    }

}
