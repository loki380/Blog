package pl.olszewski.Blog.controller.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.olszewski.Blog.domain.Author;
import pl.olszewski.Blog.domain.Post;
import pl.olszewski.Blog.helpers.*;
import pl.olszewski.Blog.service.Menagers.*;
import pl.olszewski.Blog.storage.StorageFileNotFoundException;
import pl.olszewski.Blog.storage.StorageService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
public class HomeController {

    private AuthorManager am;
    private PostManager pm;
    private PostAuthorManager pam;
    private CommentManager cm;
    private AttachmentManager atm;
    private final StorageService storageService;

    @Autowired
    public HomeController(AuthorManager am, PostManager pm, CommentManager cm, PostAuthorManager pam, AttachmentManager atm, StorageService storageService) {
        this.am = am;
        this.pm = pm;
        this.cm = cm;
        this.pam = pam;
        this.atm = atm;
        this.storageService = storageService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("posts", pm.getAllPosts());
        model.addAttribute("pam", pam);
        model.addAttribute("am", am);
        model.addAttribute("filter", new Search());
        model.addAttribute("storageService", storageService);
        return "home";
    }
    @GetMapping("/sort/number")
    public String sortNumber(Model model) {
        List<Post> posts = new ArrayList<>(pm.getAllPosts());
        Collections.sort(posts, new SortByNumberPost());
        model.addAttribute("pam", pam);
        model.addAttribute("am", am);
        model.addAttribute("posts", posts);
        model.addAttribute("filter", new Search());
        model.addAttribute("storageService", storageService);
        return "home";
    }
    @GetMapping("/sort/number_reverse")
    public String sortNumberReverse(Model model) {
        List<Post> posts = new ArrayList<>(pm.getAllPosts());
        Collections.sort(posts, new SortByNumberPostReverse());
        model.addAttribute("pam", pam);
        model.addAttribute("am", am);
        model.addAttribute("posts", posts);
        model.addAttribute("filter", new Search());
        model.addAttribute("storageService", storageService);
        return "home";
    }
    @GetMapping("/sort/author")
    public String sortAuthor(Model model) {
        List<Post> posts = new ArrayList<>(pm.getAllPosts());
        Collections.sort(posts, new SortByAuthor(am,pam));
        model.addAttribute("pam", pam);
        model.addAttribute("am", am);
        model.addAttribute("posts", posts);
        model.addAttribute("filter", new Search());
        model.addAttribute("storageService", storageService);
        return "home";
    }
    @GetMapping("/sort/author_reverse")
    public String sortAuthorReverse(Model model) {
        List<Post> posts = new ArrayList<>(pm.getAllPosts());
        Collections.sort(posts, new SortByAuthorReverse(am,pam));
        model.addAttribute("pam", pam);
        model.addAttribute("am", am);
        model.addAttribute("posts", posts);
        model.addAttribute("filter", new Search());
        model.addAttribute("storageService", storageService);
        return "home";
    }
    @GetMapping("/sort/content")
    public String sortContent(Model model) {
        List<Post> posts = new ArrayList<>(pm.getAllPosts());
        Collections.sort(posts, new SortByContent());
        model.addAttribute("pam", pam);
        model.addAttribute("am", am);
        model.addAttribute("posts", posts);
        model.addAttribute("filter", new Search());
        model.addAttribute("storageService", storageService);
        return "home";
    }
    @GetMapping("/sort/content_reverse")
    public String sortContentReverse(Model model) {
        List<Post> posts = new ArrayList<>(pm.getAllPosts());
        Collections.sort(posts, new SortByContentReverse());
        model.addAttribute("pam", pam);
        model.addAttribute("am", am);
        model.addAttribute("posts", posts);
        model.addAttribute("filter", new Search());
        model.addAttribute("storageService", storageService);
        return "home";
    }
    @PostMapping("/search")
    public String searchPosts(Search search, Model model) {
        List<Post> posts = new ArrayList<>();
        for(Post post:pm.getAllPosts()){
            System.out.println();
            if(post.getPost_content().contains(search.getFilter()) || post.getTags().contains(search.getFilter()) || String.valueOf(post.getId()).equals(search.getFilter())){
                posts.add(post);
            }
        }
        model.addAttribute("pam", pam);
        model.addAttribute("am", am);
        model.addAttribute("posts", posts);
        model.addAttribute("filter", search);
        model.addAttribute("storageService", storageService);
        return "home";
    }
    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }

    @ModelAttribute("User")
    public Author authentication(Authentication authentication) {
        if (authentication != null)
            return am.findByUsername(authentication.getName());
        else
            return null;
    }
}
