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
import static org.mockito.Mockito.*;


@GraphQlTest(AuthorController.class)
public class AuthorControllerTest {
    @Autowired
    private GraphQlTester tester;

    @MockBean
    private AuthorService authorService;

    // NOTE: Replace string below with multiline string if using Java 13+
    private final String readAllQuery = "query {\n"
            + "  allAuthors {\n"
            + "    id,\n"
            + "    firstName,\n"
            + "    lastName\n"
            + "  }\n"
            + "}";

    private final String readByIdQuery = "query AuthorByID($id: ID!) {\n"
            + "  authorById(id: $id) {\n"
            + "    id,\n"
            + "    firstName,\n"
            + "    lastName\n"
            + "  }"
            + "}";

    private final String createMutation = "mutation CreateAuthor($id: ID!, $firstname: String!, $lastname: String!) {\n" +
            "    createAuthor(id: $id, firstName: $firstname, lastName: $lastname) {\n" +
            "      id,\n" +
            "      firstName,\n" +
            "      lastName\n" +
            "    }\n" +
            "}";

    private final String updateMutation = "mutation UpdateAuthor($id: ID!, $firstname: String, $lastname: String) {\n" +
            "    updateAuthor(id: $id, firstName: $firstname, lastName: $lastname) {\n" +
            "      id,\n" +
            "      firstName,\n" +
            "      lastName\n" +
            "    }\n" +
            "}";

    private final String deleteMutation = "mutation DeleteAuthorById($id: ID!) {\n" +
            "    deleteAuthor(id: $id) {\n" +
            "        id,\n" +
            "        firstName,\n" +
            "        lastName\n" +
            "    }\n" +
            "}";

    @Test
    void ListAllAuthorsEmpty() {
        when(authorService.getAuthors()).thenReturn(List.of());
        tester.document(readAllQuery)
                .execute()
                .path("allAuthors")
                .entityList(Author.class)
                .hasSize(0);
    }

    @Test
    void ListAllAuthors() {
        List<Author> authors = List.of(
                new Author("author-5", "Harper", "Lee"),
                new Author("author-6", "Aldous", "Huxley")
        );
        when(authorService.getAuthors()).thenReturn(authors);
        tester.document(readAllQuery)
                .execute()
                .path("allAuthors")
                .entityList(Author.class)
                .hasSize(2);

    }

    @Test
    void getAuthorByIdNotFound() {
        String errorMessage = "Could not find author with ID 'author-5'";
        doThrow(new IdNotFoundException(errorMessage)).when(authorService).getById(any(String.class));
        tester.document(readByIdQuery)
                .variable("id", "author-5")
                .execute()
                .errors()
                .expect(error -> Objects.requireNonNull(error.getMessage()).equals(errorMessage));
    }

    @Test
    void getAuthorById() {
        Author author = new Author("author-12", "Fyodor", "Dostoevsky");
        when(authorService.getById(any(String.class))).thenReturn(author);
        Author result = tester.document(readByIdQuery)
                .variable("id", "author-12")
                .execute()
                .path("authorById")
                .entity(Author.class)
                .get();
        assertEquals("author-12", result.getId());
        assertEquals("Fyodor", result.getFirstName());
        assertEquals("Dostoevsky", result.getLastName());
    }

    @Test
    void createAuthorExistingID() {
        Author existing = new Author("author-84", "George", "Orwell");
        when(authorService.getById(any(String.class))).thenReturn(existing);

        tester.document(createMutation)
                .variable("id", "author-84")
                .variable("firstname", "Frank")
                .variable("lastname", "Herbert") //different author same ID
                .execute()
                .errors()
                .expect(error -> Objects.requireNonNull(error.getMessage()).equals("Author with id: 'author-84' already exists"));
    }

    @Test
    void createAuthor() {
        Author author = new Author("author-84", "George", "Orwell");
        doThrow(new IdNotFoundException("Author with 'author-84' could not be found")).when(authorService).getById(any(String.class));
        when(authorService.save(any(Author.class))).thenReturn(author);
        GraphQlTester localTester = tester.mutate().build();
        Author result = localTester
                .document(createMutation)
                .variable("id", "author-84")
                .variable("firstname", "George")
                .variable("lastname", "Orwell")
                .execute()
                .path("createAuthor")
                .entity(Author.class)
                .get();

        assertEquals("author-84", result.getId());
        assertEquals("George", result.getFirstName());
        assertEquals("Orwell", result.getLastName());
    }

    @Test
    void updateAuthorNotFound() {
        doThrow(new IdNotFoundException("Could not find author with ID 'author-72'")).when(authorService).getById(any(String.class));
        GraphQlTester localTester = tester.mutate().build();
        localTester
                .document(updateMutation)
                .variable("id", "author-72")
                .variable("firstname", "Ernest")
                .variable("lastname", "Hemingway")
                .execute()
                .errors()
                .expect(error -> {
                    System.out.println(error.getMessage());
                    return Objects.requireNonNull(error.getMessage()).equals("Could not find author with ID 'author-72'");
                });

    }

    @Test
    void updateAuthor() {
        Author original = new Author("author-72", "Ernest", "Hemingway");
        Author updated = new Author("author-72", "Chinua", "Achebe");
        when(authorService.getById(any(String.class))).thenReturn(original);
        when(authorService.save(any(Author.class))).thenReturn(updated);
        GraphQlTester localTester = tester.mutate().build();
        Author result = localTester
                .document(updateMutation)
                .variable("id", "author-72")
                .variable("firstname", "Chinua")
                .variable("lastname", "Achebe")
                .execute()
                .path("updateAuthor")
                .entity(Author.class)
                .get();
        assertEquals("author-72", result.getId());
        assertEquals("Chinua", result.getFirstName());
        assertEquals("Achebe", result.getLastName());
    }

    @Test
    void deleteAuthorNotFound() {
        doThrow(new IdNotFoundException("Could not find author with ID 'author-153'")).when(authorService).getById(any(String.class));
        GraphQlTester localTester = tester.mutate().build();
        localTester
                .document(deleteMutation)
                .variable("id", "author-153")
                .execute()
                .errors()
                //.satisfy(System.out::println)
                .expect(error -> {
                    //System.out.println(error.getErrorType() + ": " + error.getMessage());
                    return Objects.requireNonNull(error.getMessage()).equals("Could not find author with ID 'author-153'");
                });
    }

    @Test
    void deleteAuthor() {
        Author author = new Author("author-153", "Roald", "Dahl");
        when(authorService.getById(any(String.class))).thenReturn(author);
        when(authorService.deleteById(any(String.class))).thenReturn(author);
        GraphQlTester localTester = tester.mutate().build();
        Author deleted = localTester
                .document(deleteMutation)
                .variable("id", "author-153")
                .execute()
                .path("deleteAuthor")
                .entity(Author.class)
                .get();
        assertEquals("author-153", deleted.getId());
        assertEquals("Roald", deleted.getFirstName());
        assertEquals("Dahl", deleted.getLastName());
    }
}
