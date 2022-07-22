package com.piedpiper.graphqldemo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class BookController {


    private BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }
    @QueryMapping
    public List<Book> allBooks() {
        //return Book.getBookList();
        return bookService.getBookList();
    }

    @QueryMapping
    public Book bookById(@Argument String id) {
        //return Book.getById(id);
        return bookService.getById(id);
    }

    @MutationMapping
    public Book createBook(@Argument String id, @Argument String name, @Argument int pageCount, @Argument String authorID) {
        Book book = new Book(id, name, pageCount, authorID);
        //Book.pushToList(book);
        return bookService.save(book);
    }

    @SchemaMapping
    public Author author(Book book) {
        return Author.getById(book.getAuthorId());
    }
}
