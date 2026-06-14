package com.niranjan.entity;

/**
 * Represents a book in the library.
 * Each book has a unique ID, title, author, ISBN, and publication year.
 */
public class Book {

    private Long id;
    private String title;
    private String author;
    private String isbn;    // International Standard Book Number (unique book identifier)
    private int publishedYear;
    private boolean available; // true = book is on the shelf, false = borrowed

    public Book() {}

    public Book(Long id, String title, String author, String isbn, int publishedYear, boolean available) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.publishedYear = publishedYear;
        this.available = available;
    }

    // --- Getters and Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public int getPublishedYear() { return publishedYear; }
    public void setPublishedYear(int publishedYear) { this.publishedYear = publishedYear; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    @Override
    public String toString() {
        return "Book{id=" + id + ", title='" + title + "', author='" + author +
               "', isbn='" + isbn + "', year=" + publishedYear + ", available=" + available + "}";
    }
}
