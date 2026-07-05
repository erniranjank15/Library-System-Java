package com.niranjan.repository;

import com.niranjan.entity.Member;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class MemberRepository {

    private static final Logger log = LoggerFactory.getLogger(MemberRepository.class);
    private final String dbUrl;

    public MemberRepository(@Value("${spring.datasource.url}") String dbUrl) {
        this.dbUrl = dbUrl;
        try { Class.forName("org.sqlite.JDBC"); } catch (ClassNotFoundException e) {
            throw new RuntimeException("SQLite JDBC driver not found", e);
        }
        String createTable = """
                CREATE TABLE IF NOT EXISTS members (
                    id    INTEGER PRIMARY KEY AUTOINCREMENT,
                    name  TEXT NOT NULL,
                    email TEXT UNIQUE NOT NULL,
                    phone TEXT
                )
                """;
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createTable);
        } catch (SQLException e) {
            throw new RuntimeException("Could not create members table", e);
        }
    }

    public List<Member> findAll() {
        List<Member> members = new ArrayList<>();
        String sql = "SELECT * FROM members";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) members.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching members", e);
        }
        return members;
    }

    public Optional<Member> findById(Long id) {
        String sql = "SELECT * FROM members WHERE id = ?";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching member by id", e);
        }
        return Optional.empty();
    }

    public Member insert(Member member) {
        String sql = "INSERT INTO members(name, email, phone) VALUES (?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, member.getName());
            ps.setString(2, member.getEmail());
            ps.setString(3, member.getPhone());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) member.setId(keys.getLong(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting member", e);
        }
        return member;
    }

    public Member update(Member member) {
        String sql = "UPDATE members SET name=?, email=?, phone=? WHERE id=?";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, member.getName());
            ps.setString(2, member.getEmail());
            ps.setString(3, member.getPhone());
            ps.setLong(4, member.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating member", e);
        }
        return member;
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM members WHERE id = ?";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting member", e);
        }
    }

    public boolean existsById(Long id) {
        String sql = "SELECT 1 FROM members WHERE id = ?";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking member existence", e);
        }
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(dbUrl);
    }

    private Member mapRow(ResultSet rs) throws SQLException {
        return new Member(
            rs.getLong("id"),
            rs.getString("name"),
            rs.getString("email"),
            rs.getString("phone")
        );
    }
}
