package com.niranjan.repository;

import com.niranjan.entity.Book;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Handles all database operations for Books.
 * Uses plain JDBC (no JPA/Hibernate) so it's easy to understand.
 */
@Repository
public class BookRepository {

    // Path to the SQLite database file
    private static final String DB_URL = "jdbc:sqlite:library.db";

    // Called once when the app starts — creates the table if it doesn't exist yet
    public BookRepository() {
        String createTable = """
                CREATE TABLE IF NOT EXISTS books (
                    id            INTEGER PRIMARY KEY AUTOINCREMENT,
                    title         TEXT    NOT NULL,
                    author        TEXT    NOT NULL,
                    isbn          TEXT    UNIQUE,
                    published_year INTEGER,
                    available     INTEGER DEFAULT 1   -- 1 = true, 0 = false (SQLite has no boolean)
                )
                """;
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createTable);
        } catch (SQLException e) {
            throw new RuntimeException("Could not create books table", e);
        }
    }

    // ---- CRUD Operations ----

    /** Returns all books from the database */
    public List<Book> findAll() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                books.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching books", e);
        }
        return books;
    }

    /** Returns a single book by its ID, or empty if not found */
    public Optional<Book> findById(Long id) {
        String sql = "SELECT * FROM books WHERE id = ?";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching book by id", e);
        }
        return Optional.empty();
    }

    /** Inserts a new book and returns it with the generated ID */
    public Book insert(Book book) {
        String sql = "INSERT INTO books(title, author, isbn, published_year, available) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setString(3, book.getIsbn());
            ps.setInt(4, book.getPublishedYear());
            ps.setInt(5, book.isAvailable() ? 1 : 0);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) book.setId(keys.getLong(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting book", e);
        }
        return book;
    }

    /** Updates an existing book's details */
    public Book update(Book book) {
        String sql = "UPDATE books SET title=?, author=?, isbn=?, published_year=?, available=? WHERE id=?";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setString(3, book.getIsbn());
            ps.setInt(4, book.getPublishedYear());
            ps.setInt(5, book.isAvailable() ? 1 : 0);
            ps.setLong(6, book.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating book", e);
        }
        return book;
    }

    /** Deletes a book by its ID */
    public void deleteById(Long id) {
        String sql = "DELETE FROM books WHERE id = ?";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting book", e);
        }
    }

    /** Checks if a book with this ID exists */
    public boolean existsById(Long id) {
        String sql = "SELECT 1 FROM books WHERE id = ?";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking book existence", e);
        }
    }

    /** Updates only the availability status of a book (used when borrowing/returning) */
    public void updateAvailability(Long bookId, boolean available) {
        String sql = "UPDATE books SET available = ? WHERE id = ?";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, available ? 1 : 0);
            ps.setLong(2, bookId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating book availability", e);
        }
    }

    // ---- Helpers ----

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    /** Converts a database row into a Book object */
    private Book mapRow(ResultSet rs) throws SQLException {
        return new Book(
            rs.getLong("id"),
            rs.getString("title"),
            rs.getString("author"),
            rs.getString("isbn"),
            rs.getInt("published_year"),
            rs.getInt("available") == 1
        );
    }
}
