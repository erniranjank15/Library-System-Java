package com.niranjan.repository;

import com.niranjan.entity.BorrowRecord;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Handles all database operations for BorrowRecords.
 */
@Repository
public class BorrowRepository {

    private static final String DB_URL = "jdbc:sqlite:library.db";

    public BorrowRepository() {
        String createTable = """
                CREATE TABLE IF NOT EXISTS borrow_records (
                    id          INTEGER PRIMARY KEY AUTOINCREMENT,
                    book_id     INTEGER NOT NULL,
                    member_id   INTEGER NOT NULL,
                    borrow_date TEXT    NOT NULL,
                    return_date TEXT             -- NULL means book is still borrowed
                )
                """;
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createTable);
        } catch (SQLException e) {
            throw new RuntimeException("Could not create borrow_records table", e);
        }
    }

    public List<BorrowRecord> findAll() {
        List<BorrowRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM borrow_records";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) records.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching borrow records", e);
        }
        return records;
    }

    public Optional<BorrowRecord> findById(Long id) {
        String sql = "SELECT * FROM borrow_records WHERE id = ?";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching borrow record by id", e);
        }
        return Optional.empty();
    }

    /** Finds the active (not yet returned) borrow record for a specific book */
    public Optional<BorrowRecord> findActiveByBookId(Long bookId) {
        String sql = "SELECT * FROM borrow_records WHERE book_id = ? AND return_date IS NULL";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching active borrow record", e);
        }
        return Optional.empty();
    }

    public BorrowRecord insert(BorrowRecord record) {
        String sql = "INSERT INTO borrow_records(book_id, member_id, borrow_date, return_date) VALUES (?, ?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, record.getBookId());
            ps.setLong(2, record.getMemberId());
            ps.setString(3, record.getBorrowDate());
            ps.setString(4, record.getReturnDate()); // null if not returned yet
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) record.setId(keys.getLong(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting borrow record", e);
        }
        return record;
    }

    /** Sets the return date on a borrow record (used when a book is returned) */
    public void setReturnDate(Long recordId, String returnDate) {
        String sql = "UPDATE borrow_records SET return_date = ? WHERE id = ?";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, returnDate);
            ps.setLong(2, recordId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating return date", e);
        }
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    private BorrowRecord mapRow(ResultSet rs) throws SQLException {
        return new BorrowRecord(
            rs.getLong("id"),
            rs.getLong("book_id"),
            rs.getLong("member_id"),
            rs.getString("borrow_date"),
            rs.getString("return_date")
        );
    }
}
