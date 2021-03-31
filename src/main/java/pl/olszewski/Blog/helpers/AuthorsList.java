package pl.olszewski.Blog.helpers;

import pl.olszewski.Blog.domain.Author;

import java.util.ArrayList;
import java.util.List;

public class AuthorsList {
    private List<Author> authors;

    public AuthorsList(List<Author> authors) {
        this.authors = authors;
    }

    public AuthorsList() {
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }
}
