package com.piedpiper.graphqldemo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    @InjectMocks
    private BookService bookService;

    @MockBean
    private BookRepository bookRepository;

    @Test
    void getAllBooksEmpty() {
        when(bookRepository.findAll()).thenReturn(new ArrayList<>());
        List<Book> books = bookService.getBookList();
        assertEquals(0, books.size());
    }

    @Test
    void getAllBooks() {
        List<Book> books = List.of(
                new Book("book-1", "Fahrenheit 451", 451, "author-1"),
                new Book("book-2", "Lord of the Flies", 326, "author-2")
        );
        when(bookRepository.findAll()).thenReturn(books);
        List<Book> results = bookService.getBookList();
        assertEquals(2, books.size());
    }

    @Test
    void getByIdNotFound() {
        doThrow(new IdNotFoundException("Could not find book with ID 'book-4'")).when(bookRepository).findById(any(String.class));
        assertThrows(IdNotFoundException.class, () -> {
            bookService.getById("book-4");
        });
    }

    @Test
    void getById() {
        Book book = new Book("book-4", "Frankenstein", 255, "author-6");
        when(bookRepository.findById(any(String.class))).thenReturn(Optional.of(book));
        Book result = bookService.getById("book-4");
        assertEquals("book-4", result.getId());
        assertEquals("Frankenstein", result.getName());
        assertEquals(255, result.getPageCount());
        assertEquals("author-6", result.getAuthorId());
    }

    @Test
    void saveBook() {
        Book book = new Book("book-15", "Pride and Prejudice", 346, "author-12");
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        Book result = bookService.save(book);
        assertEquals("book-15", result.getId());
        assertEquals("Pride and Prejudice", result.getName());
        assertEquals(346, result.getPageCount());
        assertEquals("author-12", result.getAuthorId());
    }

    @Test
    void deleteByIdNotFound() {
        doThrow(new IdNotFoundException("Could not find book with ID 'book-10'")).when(bookRepository).findById(any(String.class));
        assertThrows(IdNotFoundException.class, () -> {
            bookService.deleteById("book-10");
        });
    }

    @Test
    void deleteById() {
        Book book = new Book("book-10", "Crime and Punishment", 289, "author-10");
        when(bookRepository.findById(any(String.class))).thenReturn(Optional.of(book));
        doNothing().when(bookRepository).deleteById(any(String.class));
        Book deleted = bookService.deleteById("book-10");
        assertEquals("book-10", deleted.getId());
        assertEquals("Crime and Punishment", deleted.getName());
        assertEquals(289, deleted.getPageCount());
        assertEquals("author-10", deleted.getAuthorId());
    }
}
