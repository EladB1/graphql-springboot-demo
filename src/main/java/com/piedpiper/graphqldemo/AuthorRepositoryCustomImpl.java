package com.piedpiper.graphqldemo;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.List;

public class AuthorRepositoryCustomImpl implements AuthorRepositoryCustom {
    @PersistenceContext
    EntityManager entityManager;

    private Author getAuthor(String authorID) {
        Query getAuthor = entityManager.createNativeQuery("SELECT * FROM author WHERE id = :id", Author.class);
        getAuthor.setParameter("id", authorID);
        try {
            Author author = (Author) getAuthor.getSingleResult();
            return author;
        }
        catch (NoResultException err) {
            return null;
        }
    }

    @Override
    @Transactional
    public void cascadingDeleteAccount(String authorID) throws IdNotFoundException {
        //Query test= entityManager.createNativeQuery("SHOW COLUMNS FROM Book"); // keeping here for future debugging
        Author author = this.getAuthor(authorID);
        if (author == null)
            throw new IdNotFoundException("Could not find author with ID '" + authorID + "'");
        Query deleteBooksWithAuthor = entityManager.createNativeQuery("DELETE FROM Book WHERE author_id = :id ");
        deleteBooksWithAuthor.setParameter("id", authorID);
        Query deleteAuthor = entityManager.createNativeQuery("DELETE FROM Author WHERE id = :id");
        deleteAuthor.setParameter("id", authorID);
        deleteBooksWithAuthor.executeUpdate();
        deleteAuthor.executeUpdate();
    }
}
