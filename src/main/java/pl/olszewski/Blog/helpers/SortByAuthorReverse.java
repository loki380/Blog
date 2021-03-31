package pl.olszewski.Blog.helpers;

import pl.olszewski.Blog.domain.Author;
import pl.olszewski.Blog.domain.Post;
import pl.olszewski.Blog.domain.PostAuthor;
import pl.olszewski.Blog.service.Menagers.AuthorManager;
import pl.olszewski.Blog.service.Menagers.PostAuthorManager;

import java.util.Comparator;

public class SortByAuthorReverse implements Comparator<Post> {

    private AuthorManager am;
    private PostAuthorManager pam;

    public SortByAuthorReverse(AuthorManager am, PostAuthorManager pam) {
        this.am = am;
        this.pam = pam;
    }

    @Override
    public int compare(Post post, Post t1) {
        int idPost = post.getId();
        int idPost1 = t1.getId();

        String delim = " ";
        StringBuilder sb = new StringBuilder();
        for(PostAuthor postAuthor : pam.findByPostId(idPost)){
            Author author = am.findById(postAuthor.getAuthor().getId()).get();
            sb.append(author.getUsername());
            sb.append(delim);
        }
        String res = sb.toString();

        StringBuilder sb1 = new StringBuilder();
        for(PostAuthor postAuthor : pam.findByPostId(idPost1)){
            Author author = am.findById(postAuthor.getAuthor().getId()).get();
            sb1.append(author.getUsername());
            sb1.append(delim);
        }
        String res1 = sb1.toString();

        return -res.compareTo(res1);
    }
}