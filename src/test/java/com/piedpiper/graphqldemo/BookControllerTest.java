package com.piedpiper.graphqldemo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.graphql.test.tester.GraphQlTester;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@GraphQlTest(BookController.class)
public class BookControllerTest {
    @Autowired
    private GraphQlTester tester;

    @MockBean
    private BookService bookService;

    @MockBean
    private AuthorService authorService;

    private final String getAllQuery = "query {\n" +
            "  allBooks {\n" +
            "    id,\n" +
            "    name,\n" +
            "    pageCount,\n" +
            "    author {\n" +
            "      id,\n" +
            "      firstName,\n" +
            "      lastName\n" +
            "    }\n" +
            "  }\n" +
            "}";

    private final String getByIdQuery = "query BookByID($id: ID!) {\n" +
            "  bookById(id: $id) {\n" +
            "    id,\n" +
            "    name,\n" +
            "    pageCount,\n" +
            "    author {\n" +
            "        id,\n" +
            "        firstName,\n" +
            "        lastName\n" +
            "    }\n" +
            "  }\n" +
            "}";

    private final String createMutation = "mutation CreateBook($id: ID!, $name: String!, $pageCount: Int!, $authorID: ID!) {\n" +
            "    createBook(id: $id, name: $name, pageCount: $pageCount, authorID: $authorID) {\n" +
            "      id,\n" +
            "      name,\n" +
            "      pageCount,\n" +
            "      author {\n" +
            "        id,\n" +
            "        firstName,\n" +
            "        lastName\n" +
            "      }\n" +
            "    }\n" +
            "}";

    private final String updateMutation = "mutation UpdateBook($id: ID!, $name: String, $pageCount: Int, $authorID: ID) {\n" +
            "    updateBook(id: $id, name: $name, pageCount: $pageCount, authorID: $authorID) {\n" +
            "      id,\n" +
            "      name,\n" +
            "      pageCount,\n" +
            "      author {\n" +
            "        id,\n" +
            "        firstName,\n" +
            "        lastName\n" +
            "      }\n" +
            "    }\n" +
            "}";

    private final String deleteMutation = "mutation DeleteBookById($id: ID!) {\n" +
            "    deleteBook(id: $id) {\n" +
            "        id,\n" +
            "        name\n" +
            "    }\n" +
            "}";

    @Test
    void getAllEmpty() {
        when(bookService.getBookList()).thenReturn(List.of());
        tester.document(getAllQuery)
                .execute()
                .path("allBooks")
                .entityList(Book.class)
                .hasSize(0);
    }

    @Test
    void getAllNonEmpty() {
        List<Book> books = List.of(
                new Book("book-11", "Grapes of Wrath", 382, "author-9"),
                new Book("book-13", "Of Mice and Men", 259, "author-12")
        );
        when(bookService.getBookList()).thenReturn(books);
        tester.document(getAllQuery)
                .execute()
                .path("allBooks")
                .entityList(Book.class)
                .hasSize(2);
    }

    @Test
    void getByIdNotFound() {
        doThrow(new IdNotFoundException("Could not find book with ID 'book-4'")).when(bookService).getById(any(String.class));
        tester.document(getByIdQuery)
                .variable("id", "book-4")
                .execute()
                .errors()
                .expect(error -> Objects.requireNonNull(error.getMessage()).equals("Could not find book with ID 'book-4'"));
    }

    @Test
    void getById() {
        Book book = new Book("book-4", "Art of War", 199, "author-2");
        //Author author = new Author("author-2", "Sun", "Tzu");
        when(bookService.getById(any(String.class))).thenReturn(book);
        //when(authorService.getById(any(String.class))).thenReturn(author);
        Book result = tester.document(getByIdQuery)
                .variable("id", "book-4")
                .execute()
                .path("bookById")
                .entity(Book.class)
                .get();
        assertEquals("book-4", result.getId());
        assertEquals("Art of War", result.getName());
        assertEquals(199, result.getPageCount());
        // NOTE: result.getAuthorId() will return null in tests but not in real code, if author exists; working on fix
    }

    @Test
    void createBookExistingBookId() {
        Book book = new Book("book-12", "Crime and Punishment", 373, "author-8");
        when(bookService.getById(any(String.class))).thenReturn(book);
        GraphQlTester localTester = tester.mutate().build();
        localTester
                .document(createMutation)
                .variable("id", "book-12")
                .variable("name", "Crime and Punishment")
                .variable("pageCount", 373)
                .variable("authorID", "author-8")
                .execute()
                .errors()
                .expect(error -> Objects.requireNonNull(error).getMessage().equals("Book with id 'book-12' already exists"));
    }

    @Test
    void createBookAuthorNotFound() {
        doThrow(new IdNotFoundException("Could not find book with ID 'book-15'")).when(bookService).getById(any(String.class));
        //when(authorService.getById(any(String.class))).thenReturn(null);
        doThrow(new IdNotFoundException("Could not find author with ID 'author-8'")).when(authorService).getById(any(String.class));
        GraphQlTester localTester = tester.mutate().build();
        localTester
                .document(createMutation)
                .variable("id", "book-15")
                .variable("name", "Crime and Punishment")
                .variable("pageCount", 373)
                .variable("authorID", "author-8")
                .execute()
                .errors()
                .expect(error -> {
                    System.out.println(error.getMessage());
                    return Objects.requireNonNull(error).getMessage().equals("Could not find author with ID 'author-8'");
                });
    }

    @Test
    void createBook() {
        doThrow(new IdNotFoundException("Could not find book with ID 'book-15'")).when(bookService).getById(any(String.class));
        Author author = new Author("author-8", "Fyodor", "Dostoevsky");
        when(authorService.getById(any(String.class))).thenReturn(author);
        Book book = new Book("book-15", "Crime and Punishment", 437, "author-8");
        when(bookService.save(any(Book.class))).thenReturn(book);
        GraphQlTester localTester = tester.mutate().build();
        Book result = localTester
                .document(createMutation)
                .variable("id", "book-15")
                .variable("name", "Crime and Punishment")
                .variable("pageCount", 373)
                .variable("authorID", "author-8")
                .execute()
                .path("createBook")
                .entity(Book.class)
                .get();
        assertEquals("book-15", result.getId());
        assertEquals("Crime and Punishment", result.getName());
        assertEquals(437, result.getPageCount());
    }

    @Test
    void updateBookNotFound() {
        doThrow(new IdNotFoundException("Could not find book with ID 'book-73'")).when(bookService).getById(any(String.class));
        GraphQlTester localTester = tester.mutate().build();
        localTester
                .document(updateMutation)
                .variable("id", "book-73")
                .variable("name", "Animal Farm")
                .variable("pageCount", 286)
                .variable("authorID", "author-16")
                .execute()
                .errors()
                .expect(error -> Objects.requireNonNull(error).getMessage().equals("Could not find book with ID 'book-73'"));
    }

    @Test
    void updateBook() {
        Book existing = new Book("book-73", "1984", 352, "author-16");
        when(bookService.getById(any(String.class))).thenReturn(existing);
        Book book = new Book("book-73", "Animal Farm", 286, "author-16");
        when(bookService.save(any(Book.class))).thenReturn(book);
        GraphQlTester localTester = tester.mutate().build();
        Book updated = localTester
                .document(updateMutation)
                .variable("id", "book-73")
                .variable("name", "Animal Farm")
                .variable("pageCount", 286)
                .variable("authorID", "author-16")
                .execute()
                .path("updateBook")
                .entity(Book.class)
                .get();
        assertEquals("book-73", updated.getId());
        assertEquals("Animal Farm", updated.getName());
        assertEquals(286, updated.getPageCount());
    }

    @Test
    void deleteBookNotFound() {
        doThrow(new IdNotFoundException("Could not find book with ID 'book-34'")).when(bookService).deleteById(any(String.class));
        GraphQlTester localTester = tester.mutate().build();
        localTester
                .document(deleteMutation)
                .variable("id", "book-34")
                .execute()
                .errors()
                .expect(error -> {
                    System.out.println(error.getMessage());
                    return Objects.requireNonNull(error).getMessage().equals("Could not find book with ID 'book-34'");
                });
    }

    @Test
    void deleteBook() {
        Book book = new Book("book-41", "To Kill a Mockingbird", 401, "author-13");
        when(bookService.getById(any(String.class))).thenReturn(book);
        when(bookService.deleteById(any(String.class))).thenReturn(book);
        GraphQlTester localTester = tester.mutate().build();
        Book result = localTester
                .document(deleteMutation)
                .variable("id", "book-41")
                .execute()
                .path("deleteBook")
                .entity(Book.class)
                .get();
        assertEquals("book-41", result.getId());
        assertEquals("To Kill a Mockingbird", result.getName());
    }
}
