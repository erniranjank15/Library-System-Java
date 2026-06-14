package com.niranjan.service;

import com.niranjan.entity.Book;
import com.niranjan.exception.ResourceNotFoundException;
import com.niranjan.repository.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Contains all the business logic for books.
 * The controller calls this, and this calls the repository.
 *
 * Flow: Controller -> Service -> Repository -> SQLite DB
 */
@Service
public class BookService {

    private static final Logger log = LoggerFactory.getLogger(BookService.class);

    private final BookRepository bookRepository;

    // Spring automatically injects BookRepository here (Dependency Injection)
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    /** Get all books */
    public List<Book> getAllBooks() {
        log.debug("Fetching all books");
        return bookRepository.findAll();
    }

    /** Get one book by ID — throws 404 if not found */
    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
    }

    /** Add a new book */
    public Book addBook(Book book) {
        // New books are available by default
        book.setAvailable(true);
        Book saved = bookRepository.insert(book);
        log.info("Added new book: id={}, title={}", saved.getId(), saved.getTitle());
        return saved;
    }

    /** Update book details */
    public Book updateBook(Long id, Book book) {
        // Make sure the book exists first
        Book existing = getBookById(id);
        book.setId(id);
        // Keep the current availability (don't let a PUT change availability)
        book.setAvailable(existing.isAvailable());
        Book updated = bookRepository.update(book);
        log.info("Updated book id={}", id);
        return updated;
    }

    /** Delete a book */
    public void deleteBook(Long id) {
        // Make sure the book exists before deleting
        getBookById(id);
        bookRepository.deleteById(id);
        log.info("Deleted book id={}", id);
    }
}
