package com.piedpiper.graphqldemo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AuthorService {
    private AuthorRepository authorRepo;

    @Autowired
    public AuthorService(AuthorRepository authorRepo) {
        this.authorRepo = authorRepo;
    }

    public List<Author> getAuthors() {
        List<Author> authors = new ArrayList<>();
        Iterable<Author> authorList = authorRepo.findAll();
        for (Author author : authorList) {
            authors.add(author);
        }
        return authors;
    }

    public Author getById(String id) {
        Optional<Author> author = authorRepo.findById(id);
        if (author.isEmpty())
            throw new IdNotFoundException("Could not find author with ID '" + id + "'");
        return author.get();
    }

    public Author save(Author author) {
        return authorRepo.save(author);
    }

    public Author deleteById(String id) throws IdNotFoundException {
        Author author = getById(id);
        if (author == null)
            throw new IdNotFoundException("Could not find Author with ID " + id);
        //authorRepo.deleteById(id);
        authorRepo.cascadingDeleteAccount(id);
        return author;
    }
}
