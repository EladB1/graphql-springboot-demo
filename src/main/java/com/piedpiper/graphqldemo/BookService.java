package com.piedpiper.graphqldemo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    private BookRepository bookRepo;

    @Autowired
    public BookService(BookRepository bookRepo) {
        this.bookRepo = bookRepo;
    }

    public List<Book> getBookList() {
        List<Book> bookList = new ArrayList<>();
        Iterable<Book> books = bookRepo.findAll();
        for (Book book : books) {
            bookList.add(book);
        }
        return bookList;
    }

    public Book getById(String id) {
        Optional<Book> book = bookRepo.findById(id);
        if (book.isEmpty())
            throw new IdNotFoundException("Could not find book with ID '" + id + "'");
        return book.get();
    }

    public Book save(Book book) {
        return bookRepo.save(book);
    }

    public Book deleteById(String id) {
        Book book = this.getById(id);
        bookRepo.deleteById(id);
        return book;
    }
}
