package com.niranjan.repository;

import com.niranjan.entity.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class BookRepository {

    private static final Logger log = LoggerFactory.getLogger(BookRepository.class);
    private final String dbUrl;

    public BookRepository(@Value("${spring.datasource.url}") String dbUrl) {
        this.dbUrl = dbUrl;
        log.info("BookRepository init with DB URL: {}", dbUrl);
        // Ensure SQLite driver is loaded
        try { Class.forName("org.sqlite.JDBC"); } catch (ClassNotFoundException e) {
            throw new RuntimeException("SQLite JDBC driver not found", e);
        }
        String createTable = """
                CREATE TABLE IF NOT EXISTS books (
                    id            INTEGER PRIMARY KEY AUTOINCREMENT,
                    title         TEXT    NOT NULL,
                    author        TEXT    NOT NULL,
                    isbn          TEXT    UNIQUE,
                    published_year INTEGER,
                    available     INTEGER DEFAULT 1
                )
                """;
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createTable);
            log.info("Books table ready");
        } catch (SQLException e) {
            log.error("Could not create books table", e);
            throw new RuntimeException("Could not create books table", e);
        }
    }

    public List<Book> findAll() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) books.add(mapRow(rs));
        } catch (SQLException e) {
            log.error("Error fetching books", e);
            throw new RuntimeException("Error fetching books", e);
        }
        return books;
    }

    public Optional<Book> findById(Long id) {
        String sql = "SELECT * FROM books WHERE id = ?";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            log.error("Error fetching book by id", e);
            throw new RuntimeException("Error fetching book by id", e);
        }
        return Optional.empty();
    }

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
            log.error("Error inserting book", e);
            throw new RuntimeException("Error inserting book", e);
        }
        return book;
    }

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
            log.error("Error updating book", e);
            throw new RuntimeException("Error updating book", e);
        }
        return book;
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM books WHERE id = ?";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            log.error("Error deleting book", e);
            throw new RuntimeException("Error deleting book", e);
        }
    }

    public boolean existsById(Long id) {
        String sql = "SELECT 1 FROM books WHERE id = ?";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            log.error("Error checking book existence", e);
            throw new RuntimeException("Error checking book existence", e);
        }
    }

    public void updateAvailability(Long bookId, boolean available) {
        String sql = "UPDATE books SET available = ? WHERE id = ?";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, available ? 1 : 0);
            ps.setLong(2, bookId);
            ps.executeUpdate();
        } catch (SQLException e) {
            log.error("Error updating book availability", e);
            throw new RuntimeException("Error updating book availability", e);
        }
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(dbUrl);
    }

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
