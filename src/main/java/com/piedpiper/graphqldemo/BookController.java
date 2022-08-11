package com.piedpiper.graphqldemo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import javax.persistence.NoResultException;
import java.util.List;

@Controller
public class BookController {


    private BookService bookService;
    private AuthorService authorService;

    @Autowired
    public BookController(BookService bookService, AuthorService authorService) {
        this.bookService = bookService;
        this.authorService = authorService;
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
    public Book createBook(@Argument String id, @Argument String name, @Argument int pageCount, @Argument String authorID) throws Exception {
        try {
            Book existingBook = bookService.getById(id);
            throw new Exception("Book with id '" + id + "' already exists"); // TODO: change this from generic exception class
        }
        catch (IdNotFoundException err) {
            Book book = new Book(id, name, pageCount, authorID);
            // try, catch block below is to make sure the authorID is already in the DB (somewhat of an ugly hack though)
            try {
                authorService.getById(authorID);
            }
            catch (IdNotFoundException authorErr) {
                throw new IdNotFoundException(authorErr.getMessage());
            }
            return bookService.save(book);
        }
    }

    @MutationMapping
    public Book updateBook(@Argument String id, @Argument String name, @Argument int pageCount, @Argument String authorID) {
        Book book = bookService.getById(id); // check if exists here, so we can still use bookService.save(book);
        if (book == null)
            throw new IdNotFoundException("Could not find book with ID '" + id + "'");
        // TODO: validate author is valid
        if (name != null && name != book.getName())
            book.setName(name);
        if (pageCount > 0 && pageCount != book.getPageCount())
            book.setPageCount(pageCount);
        if (authorID != null && authorID != book.getAuthorId())
            book.setAuthorId(authorID);
        return bookService.save(book);
    }

    @MutationMapping
    public Book deleteBook(@Argument String id) {
        return bookService.deleteById(id);
    }

    @MutationMapping
    public Book createBookAndAuthor(@Argument BookAndAuthorInput input) {
        // create book and author at the same time rather than doing it separately
        Author author = new Author(input.getAuthorId(), input.getFirstName(), input.getLastName());
        Book book = new Book(input.getBookId(), input.getBookName(), input.getPageCount(), input.getAuthorId());
        authorService.save(author);
        return bookService.save(book);
    }

    @SchemaMapping
    public Author author(Book book) {
        return authorService.getById(book.getAuthorId());
    }
}
