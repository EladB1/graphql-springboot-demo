package com.piedpiper.graphqldemo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;


@Controller
public class AuthorController {
    private AuthorService authorService;

    @Autowired
    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @QueryMapping
    public List<Author> allAuthors() {
        return authorService.getAuthors();
    }

    @QueryMapping
    public Author authorById(@Argument String id) {
        return authorService.getById(id);
    }

    @MutationMapping
    public Author createAuthor(@Argument String id, @Argument String firstName, @Argument String lastName) throws Exception {
        try {
            Author existingAuthor = authorService.getById(id); // IdNotFoundException thrown by this method
            throw new Exception("Author with id: '" + id + "' already exists");
        }
        catch (IdNotFoundException err) {
            Author author = new Author(id, firstName, lastName);
            return authorService.save(author);
        }
    }

    @MutationMapping
    public Author updateAuthor(@Argument String id, @Argument String firstName, @Argument String lastName) {
        Author author = authorService.getById(id); // IdNotFoundException thrown by this method
        if (firstName != null && author.getFirstName() != firstName)
            author.setFirstName(firstName);
        if (lastName != null && author.getLastName() != lastName)
            author.setLastName(lastName);
        return authorService.save(author);
    }

    @MutationMapping
    public Author deleteAuthor(@Argument String id) throws IdNotFoundException {
        Author deleted = authorService.deleteById(id);
        if (deleted == null)
            throw new IdNotFoundException("Could not find author with ID '" + id + "'");
        return deleted;
    }

}
