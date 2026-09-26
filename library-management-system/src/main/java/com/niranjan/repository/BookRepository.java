package com.niranjan.repository;

import com.niranjan.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository for Book entity.
 * Provides all CRUD operations automatically without writing any raw SQL queries.
 */
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByIsbn(String isbn);

    List<Book> findByAvailable(boolean available);

    // Convenience aliases for existing service method compatibility
    default Book insert(Book book) {
        return save(book);
    }

    default Book update(Book book) {
        return save(book);
    }

    default void updateAvailability(Long bookId, boolean available) {
        findById(bookId).ifPresent(book -> {
            book.setAvailable(available);
            save(book);
        });
    }
}
