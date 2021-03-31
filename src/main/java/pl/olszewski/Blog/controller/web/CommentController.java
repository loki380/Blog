package pl.olszewski.Blog.controller.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import pl.olszewski.Blog.domain.Comment;
import pl.olszewski.Blog.dto.UserRegistrationDto;
import pl.olszewski.Blog.service.Menagers.AuthorManager;
import pl.olszewski.Blog.service.Menagers.CommentManager;
import pl.olszewski.Blog.service.Menagers.PostAuthorManager;
import pl.olszewski.Blog.service.Menagers.PostManager;

import javax.validation.Valid;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

@Controller("commentwebcontroller")
public class CommentController {
    private AuthorManager am;
    private PostManager pm;
    private PostAuthorManager pam;
    private CommentManager cm;

    @Autowired
    public CommentController(AuthorManager am, PostManager pm, PostAuthorManager pam, CommentManager cm) {
        this.am = am;
        this.pm = pm;
        this.pam= pam;
        this.cm= cm;
    }
    @PostMapping("/comment/new/{id}")
    public String addComment(@PathVariable("id") int id, @Valid @ModelAttribute("newComment") Comment newComment, BindingResult errComment, Model model, @CurrentSecurityContext(expression="authentication") Authentication authentication) {
        if(errComment.hasErrors()){
            model.addAttribute("post", pm.findById(id).get());
            model.addAttribute("comments", cm.findByPostId(id));
            model.addAttribute("newComment", newComment);
            return "post";
        }
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        String role = "";
        for (GrantedAuthority gauth : authorities) {
            role = gauth.getAuthority();
        }
        if(role.equals("ROLE_ADMIN") || role.equals("ROLE_USER")){
            newComment.setUsername(authentication.getName());
            Comment comment = new Comment();
            comment.setUsername(newComment.getUsername());
            comment.setComment_content(newComment.getComment_content());
            comment.setIdPost(id);
            comment.setPost(pm.findById(id).get());
            cm.addComment(comment);
            return "redirect:/post/{id}";
        }
        else{
            Comment comment = new Comment();
            comment.setUsername(newComment.getUsername());
            comment.setComment_content(newComment.getComment_content());
            comment.setIdPost(id);
            comment.setPost(pm.findById(id).get());
            cm.addComment(comment);

            UserRegistrationDto user = new UserRegistrationDto();
            user.setUsername(newComment.getUsername());
            model.addAttribute("user", user);
            return "registration";
        }
    }
    @GetMapping("/comment/delete/{id}")
    public String deleteComment(@PathVariable("id") int id, @CurrentSecurityContext(expression="authentication") Authentication authentication) {
        boolean tmp = cm.findById(id).get().getUsername().equals(authentication.getName());
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String role = "";
        for (GrantedAuthority gauth : authorities) {
            role = gauth.getAuthority();
        }
        if(tmp || role.equals("ROLE_ADMIN")) {
            int idPost = cm.findById(id).get().getIdPost();
            cm.removeComment(id);
            return "redirect:/post/"+idPost;
        }
        else{
            return "Access";
        }
    }
    @GetMapping("/comment/edit/{id}")
    public String editComment(@PathVariable("id") int id, Model model, @CurrentSecurityContext(expression="authentication") Authentication authentication) {
        boolean tmp = cm.findById(id).get().getUsername().equals(authentication.getName());
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String role = "";
        for (GrantedAuthority gauth : authorities) {
            role = gauth.getAuthority();
        }
        if(tmp || role.equals("ROLE_ADMIN")) {
            model.addAttribute("comment", cm.findById(id).get());
            return "comment-edit";
        }
        else{
            return "Access";
        }
    }
    @PostMapping("/comment/edit/{id}")
    public String changeComment(@PathVariable("id") String id, @Valid Comment comment, BindingResult errComment) {
        if(errComment.hasErrors()){
            return "comment-edit";
        }
        int idComment = Integer.parseInt(id);
        int idPost = cm.findById(idComment).get().getIdPost();
        cm.findById(idComment).get().setComment_content(comment.getComment_content());
        cm.findById(idComment).get().setUsername(comment.getUsername());
        return "redirect:/post/"+idPost;
    }
    @GetMapping("/comment/users/{user}")
    public String userComments(@PathVariable("user") String user, Model model) {
        int count=0;
        for(Comment comment: cm.getAllComment()){
            if(comment.getUsername().equals(user)){
                count++;
            }
        }
        model.addAttribute("comments", cm.findByUsername(user));
        model.addAttribute("username", user);
        model.addAttribute("amount", count);
        return "comments";
    }
}
