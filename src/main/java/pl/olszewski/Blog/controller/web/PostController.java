package pl.olszewski.Blog.controller.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.olszewski.Blog.domain.*;
import pl.olszewski.Blog.helpers.AuthorsList;
import pl.olszewski.Blog.helpers.Search;
import pl.olszewski.Blog.service.Menagers.*;
import pl.olszewski.Blog.storage.StorageService;

import javax.validation.Valid;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Controller("postwebcontroller")
public class PostController {

    private AuthorManager am;
    private PostManager pm;
    private PostAuthorManager pam;
    private CommentManager cm;
    private AttachmentManager atm;
    private final StorageService storageService;

    @Autowired
    public PostController(AuthorManager am, PostManager pm, PostAuthorManager pam, CommentManager cm, AttachmentManager atm, StorageService storageService) {
        this.am = am;
        this.pm = pm;
        this.pam= pam;
        this.cm= cm;
        this.atm= atm;
        this.storageService= storageService;
    }

    @GetMapping("/post/{id}")
    public String detailsPost(@PathVariable("id") String id, Model model) {
        int idPost = Integer.parseInt(id);
        List<Author> authors = new ArrayList<>();
        for(PostAuthor postAuthor : pam.findByPostId(idPost)){
            authors.add(am.findById(postAuthor.getAuthor().getId()).get());
        }
        model.addAttribute("authors", authors);
        model.addAttribute("post", pm.findById(idPost).get());
        model.addAttribute("comments", cm.findByPostId(idPost));
        model.addAttribute("newComment", new Comment());
        model.addAttribute("attachments", atm.getAllByPost(idPost));
        return "post";
    }
    @GetMapping("/post/new")
    public String newPost(Model model){
        List<String> tagsWithDuplication = new ArrayList<>();
        pm.getAllPosts().forEach(t -> tagsWithDuplication.addAll(Arrays.asList(t.getTags().split(" "))));
        List<String> tags = new ArrayList<>(new HashSet<>(tagsWithDuplication));
        Collections.sort(tags);
        model.addAttribute("post", new Post());
        model.addAttribute("tags", tags);
        return "post-new";
    }
    @PostMapping("/post/new")
    public String addPost(@Valid Post post, BindingResult errPost, @CurrentSecurityContext(expression="authentication.name")
            String username, @RequestParam("file") MultipartFile[] files) {
        if(errPost.hasErrors()){
            return "post-new";
        }
        storageService.store(files);
        post.setTags(post.getTags().replace(","," "));

        atm.add(files, post.getId());
        pm.addPost(post);

        PostAuthorId pa = new PostAuthorId(post.getId(),am.findByUsername(username).getId());
        pam.addPostAuthor(new PostAuthor(pa,post,am.findByUsername(username)));

        return "redirect:/";
    }
    @GetMapping("/post/delete/{id}")
    public String deletePost(@PathVariable("id") int id, Comment comment, @CurrentSecurityContext(expression="authentication") Authentication authentication) {
        AtomicBoolean tmp = new AtomicBoolean(false);
        pam.findByPostId(id).forEach(t -> {
            if(t.getAuthor().getUsername().equals(authentication.getName())) tmp.set(true);
        });
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String role = "";
        for (GrantedAuthority gauth : authorities) {
            role = gauth.getAuthority();
        }
        if(tmp.get() || role.equals("ROLE_ADMIN")){
            atm.deleteAllByPostId(id);
            cm.deleteAllByPostId(id);
            pam.deleteAllByPostId(id);
            pm.removePost(id);
            return "redirect:/";
        }
        else{
            return "Access";
        }
    }
    @GetMapping("/post/edit/{id}")
    public String editPost(@PathVariable("id") int id, Model model, @CurrentSecurityContext(expression="authentication") Authentication authentication) {
        AtomicBoolean tmp = new AtomicBoolean(false);
        pam.findByPostId(id).forEach(t -> {
            if(t.getAuthor().getUsername().equals(authentication.getName())) tmp.set(true);
        });
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        String role = "";
        for (GrantedAuthority gauth : authorities) {
            role = gauth.getAuthority();
        }
        if(tmp.get() || role.equals("ROLE_ADMIN")){
            ArrayList<Author> authors = new ArrayList<>();
            for(PostAuthor postAuthor : pam.findByPostId(id)){
                authors.add(am.findById(postAuthor.getAuthor().getId()).get());
            }
            model.addAttribute("form", new AuthorsList(authors));
            model.addAttribute("post", pm.findById(id).get());
            model.addAttribute("attachments", atm.getAllByPost(id));
            return "post-edit";
        }
        else{
            return "Access";
        }
    }
    @PostMapping("/post/edit/{id}")
    public String changePost(@PathVariable("id") String id, AuthorsList authors,@Valid Post post, BindingResult errPost, @RequestParam("file") MultipartFile[] files,Model model) {
        if(errPost.hasErrors()){
            model.addAttribute("form", authors);
            model.addAttribute("post", post);
            return "post-edit";
        }
        storageService.store(files);
        int idPost = Integer.parseInt(id);
        if(authors.getAuthors() != null){
            for(Author author : authors.getAuthors()){
                am.findById(author.getId()).get().setUsername(author.getUsername());
                am.findById(author.getId()).get().setFirst_name(author.getFirst_name());
                am.findById(author.getId()).get().setLast_name(author.getLast_name());
            }
        }
        pm.findById(idPost).get().setPost_content(post.getPost_content());
        pm.findById(idPost).get().setTags(post.getTags());
        atm.add(files, idPost);

        return "redirect:/post/{id}";
    }

    @GetMapping("/post/tags/{tagg}")
    public String tagPost(@PathVariable("tagg") String tagg, Model model) {
        model.addAttribute("posts", pm.getPostsByTag(tagg));
        model.addAttribute("pam", pam);
        model.addAttribute("am", am);
        model.addAttribute("filter", new Search());
        model.addAttribute("storageService", storageService);
        return "home";
    }
    @GetMapping("/author/{id}")
    public String authorInfo(@PathVariable("id") String id, Model model) {
        int idAuthor = Integer.parseInt(id);
        List<Post> posts = new ArrayList<>();
        int count=0;
        for(PostAuthor postAuthor: pam.getAllPostAuthor()){
            if(postAuthor.getAuthor().getId()==idAuthor){
                count++;
                posts.add(pm.findById(postAuthor.getPost().getId()).get());
            }
        }
        model.addAttribute("author", am.findById(idAuthor).get());
        model.addAttribute("posts", posts);
        model.addAttribute("amount", count);
        return "author";
    }
    @GetMapping("/post/attachment/delete/{filename}")
    public String deleteAttachment(@PathVariable("filename") String filename) {
        int idPost = atm.findByFileName(filename).getIdPost();
        atm.remove(filename);
        return "redirect:/post/edit/"+idPost;
    }
}
