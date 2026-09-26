package com.niranjan.entity;

import jakarta.persistence.*;

/**
 * Tracks which member borrowed which book and when.
 * Mapped to 'borrow_records' table in PostgreSQL via JPA / Hibernate.
 */
@Entity
@Table(name = "borrow_records")
public class BorrowRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "book_id", nullable = false)
    private Long bookId;       // which book was borrowed

    @Column(name = "member_id", nullable = false)
    private Long memberId;     // who borrowed it

    @Column(name = "borrow_date", nullable = false, length = 50)
    private String borrowDate; // e.g. "2024-06-01"

    @Column(name = "return_date", length = 50)
    private String returnDate; // null means book is still borrowed

    public BorrowRecord() {}

    public BorrowRecord(Long id, Long bookId, Long memberId, String borrowDate, String returnDate) {
        this.id = id;
        this.bookId = bookId;
        this.memberId = memberId;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
    }

    // --- Getters and Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getBookId() { return bookId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }

    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }

    public String getBorrowDate() { return borrowDate; }
    public void setBorrowDate(String borrowDate) { this.borrowDate = borrowDate; }

    public String getReturnDate() { return returnDate; }
    public void setReturnDate(String returnDate) { this.returnDate = returnDate; }

    @Override
    public String toString() {
        return "BorrowRecord{id=" + id + ", bookId=" + bookId + ", memberId=" + memberId +
               ", borrowDate='" + borrowDate + "', returnDate='" + returnDate + "'}";
    }
}
