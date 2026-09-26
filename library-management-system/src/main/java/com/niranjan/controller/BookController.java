package com.niranjan.controller;

import com.niranjan.entity.Book;
import com.niranjan.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for Book operations.
 *
 * Base URL: /api/books
 *
 * Endpoints:
 *   GET    /api/books          -> get all books
 *   GET    /api/books/{id}     -> get one book
 *   POST   /api/books          -> add a new book
 *   PUT    /api/books/{id}     -> update a book
 *   DELETE /api/books/{id}     -> delete a book
 */
@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    // GET /api/books
    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    // GET /api/books/1
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBook(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    // POST /api/books
    // Body: { "title": "Clean Code", "author": "Robert Martin", "isbn": "978-0132350884", "publishedYear": 2008 }
    @PostMapping
    public ResponseEntity<Book> addBook(@RequestBody Book book) {
        Book created = bookService.addBook(book);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // PUT /api/books/1
    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable Long id, @RequestBody Book book) {
        return ResponseEntity.ok(bookService.updateBook(id, book));
    }

    // DELETE /api/books/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.ok(Map.of("message", "Book deleted successfully"));
    }
}
