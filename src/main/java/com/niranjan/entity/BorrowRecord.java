package com.niranjan.entity;

/**
 * Tracks which member borrowed which book and when.
 * A record is "open" when returnDate is null (book not yet returned).
 */
public class BorrowRecord {

    private Long id;
    private Long bookId;       // which book was borrowed
    private Long memberId;     // who borrowed it
    private String borrowDate; // e.g. "2024-06-01"
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
