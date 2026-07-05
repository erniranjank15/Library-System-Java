package com.niranjan.repository;

import com.niranjan.entity.BorrowRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class BorrowRepository {

    private final String dbUrl;

    public BorrowRepository(@Value("${spring.datasource.url}") String dbUrl) {
        this.dbUrl = dbUrl;

        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("SQLite JDBC Driver not found", e);
        }

        createTable();
    }

    private void createTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS borrow_records (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    book_id INTEGER NOT NULL,
                    member_id INTEGER NOT NULL,
                    borrow_date TEXT NOT NULL,
                    return_date TEXT
                )
                """;

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);

        } catch (SQLException e) {
            throw new RuntimeException("Error creating borrow_records table", e);
        }
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(dbUrl);
    }

    public List<BorrowRecord> findAll() {
        List<BorrowRecord> list = new ArrayList<>();

        String sql = "SELECT * FROM borrow_records";

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching borrow records", e);
        }

        return list;
    }

    public Optional<BorrowRecord> findById(Long id) {
        String sql = "SELECT * FROM borrow_records WHERE id=?";

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching borrow record", e);
        }

        return Optional.empty();
    }

    public BorrowRecord insert(BorrowRecord record) {

        String sql = """
                INSERT INTO borrow_records
                (book_id, member_id, borrow_date, return_date)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, record.getBookId());
            ps.setLong(2, record.getMemberId());
            ps.setString(3, record.getBorrowDate());
            ps.setString(4, record.getReturnDate());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    record.setId(keys.getLong(1));
                }
            }

            return record;

        } catch (SQLException e) {
            throw new RuntimeException("Error inserting borrow record", e);
        }
    }

    public Optional<BorrowRecord> findActiveByBookId(Long bookId) {

        String sql = """
                SELECT *
                FROM borrow_records
                WHERE book_id = ?
                AND return_date IS NULL
                """;

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, bookId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching active borrow record", e);
        }

        return Optional.empty();
    }

    public Optional<BorrowRecord> findActiveByMemberId(Long memberId) {

        String sql = """
                SELECT *
                FROM borrow_records
                WHERE member_id = ?
                AND return_date IS NULL
                """;

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, memberId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching active borrow record", e);
        }

        return Optional.empty();
    }

    public List<BorrowRecord> findAllByBookId(Long bookId) {

        List<BorrowRecord> list = new ArrayList<>();

        String sql = "SELECT * FROM borrow_records WHERE book_id=?";

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, bookId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    list.add(mapRow(rs));
                }

            }

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching borrow records", e);
        }

        return list;
    }

    public List<BorrowRecord> findAllByMemberId(Long memberId) {

        List<BorrowRecord> list = new ArrayList<>();

        String sql = "SELECT * FROM borrow_records WHERE member_id=?";

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, memberId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    list.add(mapRow(rs));
                }

            }

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching borrow records", e);
        }

        return list;
    }

    public void setReturnDate(Long recordId, String returnDate) {

        String sql = "UPDATE borrow_records SET return_date=? WHERE id=?";

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, returnDate);
            ps.setLong(2, recordId);

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error updating return date", e);
        }
    }

   
    

    public void deleteById(Long id) {

        String sql = "DELETE FROM borrow_records WHERE id=?";

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting borrow records by member id", e);
        }
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