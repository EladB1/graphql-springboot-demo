package com.piedpiper.graphqldemo;

public class BookAndAuthorInput {
    private String bookId;
    private String bookName;
    private int pageCount;
    private String authorId;
    private String firstName;
    private String lastName;

    public BookAndAuthorInput() {

    }


    public BookAndAuthorInput(String bookId, String bookName, int pageCount, String authorId, String firstName, String lastName) {
        this.bookId = bookId;
        this.bookName = bookName;
        this.pageCount = pageCount;
        this.authorId = authorId;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
