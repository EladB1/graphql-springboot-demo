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
    public Author createAuthor(@Argument String id, @Argument String firstName, @Argument String lastName) {
        Author author = new Author(id, firstName, lastName);
        return authorService.save(author);
    }

    @MutationMapping
    public Author updateAuthor(@Argument String id, @Argument String firstName, @Argument String lastName) {
        Author author = authorService.getById(id);
        if (author == null)
            throw new IdNotFoundException("Could not find Author with ID " + id); // throw Exception here instead of service to simplify
        if (firstName != null && author.getFirstName() != firstName)
            author.setFirstName(firstName);
        if (lastName != null && author.getLastName() != lastName)
            author.setLastName(lastName);
        return authorService.save(author);
    }

    @MutationMapping
    public Author deleteAuthor(@Argument String id) throws IdNotFoundException {
        return authorService.deleteById(id);
    }

}
