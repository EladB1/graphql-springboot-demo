package com.piedpiper.graphqldemo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
public class AuthorServiceTest {

    @InjectMocks
    private AuthorService authorService;

    @MockBean(AuthorRepository.class)
    private AuthorRepository authorRepository;

    @Test
    void getAllAuthorsEmpty() {
        List<Author> authors = new ArrayList<>();
        when(authorRepository.findAll()).thenReturn(new ArrayList<>());
        List<Author> getAuthorsResults = authorService.getAuthors();
        assertEquals(authors, getAuthorsResults);
    }

    @Test
    void getAllAuthorsNonEmpty() {
        List<Author> authors = List.of(
          new Author("author-1", "Jane", "Austen"),
          new Author("author-2", "Franz", "Kafka")
        );
        when(authorRepository.findAll()).thenReturn(authors);
        List<Author> getAuthorsResults = authorService.getAuthors();
        assertEquals(2, getAuthorsResults.size());
    }

    @Test
    void getByIdNoResult() {
        doThrow(new IdNotFoundException("Could not find Author with ID 'author-3'")).when(authorRepository).findById(any(String.class));
        assertThrows(IdNotFoundException.class, () -> {
            Author author = authorService.getById("author-3");
        });
    }

    @Test
    void getById() {
        Author author = new Author("author-3", "Miguel", "de Cervantes");
        when(authorRepository.findById(any(String.class))).thenReturn(Optional.of(author));
        Author result = authorService.getById("author-3");
        assertNotNull(result);
        assertEquals("de Cervantes", result.getLastName());
    }

    @Test
    void saveAuthor() {
        Author author = new Author("author-14", "Mary", "Shelley");
        when(authorRepository.save(any(Author.class))).thenReturn(author);
        Author saved = authorService.save(author);
        assertEquals("author-14", saved.getId());
        assertEquals("Mary", saved.getFirstName());
        assertEquals("Shelley", saved.getLastName());
    }

    @Test
    void deleteByIdNotFound() {
        doThrow(new IdNotFoundException("Could not find author with ID 'author-25'")).when(authorRepository).findById(any(String.class));
        assertThrows(IdNotFoundException.class, () -> {
            authorService.deleteById("author-25");
        });
    }

    @Test
    void deleteById() {
        Author author = new Author("author-25", "William", "Golding");
        when(authorRepository.findById(any(String.class))).thenReturn(Optional.of(author));
        doNothing().when(authorRepository).cascadingDeleteAccount(any(String.class));
        Author deleted = authorService.deleteById("author-25");
        assertEquals("author-25", deleted.getId());
        assertEquals("William", deleted.getFirstName());
        assertEquals("Golding", deleted.getLastName());
    }

}
