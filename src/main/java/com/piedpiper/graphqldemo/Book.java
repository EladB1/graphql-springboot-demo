package com.piedpiper.graphqldemo;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Arrays;
import java.util.List;

@Entity
public class Book {
    @Id
    private String id;
    private String name;
    private int pageCount;
    private String authorId;

    public Book() {

    }

    public Book(String id, String name, int pageCount, String authorId) {
        this.id = id;
        this.name = name;
        this.pageCount = pageCount;
        this.authorId = authorId;
    }

    private static List<Book> books = Arrays.asList(
        new Book("book-1", "1984", 206, "author-1"),
        new Book("book-2", "Fahrenheit 451", 317, "author-2"),
        new Book("book-3", "Frankenstein", 199, "author-3")
    );

    public static List<Book> getBookList() {
        return Book.books;
    }

    public static Book pushToList(Book book) {
        books.add(book);
        return book;
    }

    public static Book removeFromList(String bookID) {
        Book book = null;
        int index = -1;
        for (int i = 0; i < books.size(); i++) {
            if (books.get(i).getId().equals(bookID)) {
                book = books.get(i);
                index = i;
                break;
            }
        }
        if (book != null) {
            if (index != -1)
                books.remove(index);
        }
        return book;
    }

    public static Book getById(String id) {
        return books.stream().filter(book -> book.getId().equals(id)).findFirst().orElse(null);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }
}
