package com.niranjan.service;

import com.niranjan.entity.Book;
import com.niranjan.entity.BorrowRecord;
import com.niranjan.exception.BadRequestException;
import com.niranjan.exception.ResourceNotFoundException;
import com.niranjan.repository.BookRepository;
import com.niranjan.repository.BorrowRepository;
import com.niranjan.repository.MemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * Handles borrowing and returning books.
 *
 * Business rules:
 *  1. A book must exist and be available to be borrowed.
 *  2. A member must exist to borrow a book.
 *  3. To return a book, the active borrow record must exist.
 *  4. To delete a borrow record, the user must have admin privileges.
 */
@Service
public class BorrowService {

    private static final Logger log = LoggerFactory.getLogger(BorrowService.class);

    private final BorrowRepository borrowRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;

    public BorrowService(BorrowRepository borrowRepository,
                         BookRepository bookRepository,
                         MemberRepository memberRepository) {
        this.borrowRepository = borrowRepository;
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
    }

    /** Returns all borrow records (history of all borrows) */
    public List<BorrowRecord> getAllRecords() {
        return borrowRepository.findAll();
    }

    /**
     * Borrow a book.
     * - Validates that the book and member exist.
     * - Checks that the book is currently available.
     * - Creates a borrow record and marks the book as unavailable.
     */
    public BorrowRecord borrowBook(Long bookId, Long memberId) {
        // 1. Check the book exists
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));

        // 2. Check the book is available
        if (!book.isAvailable()) {
            throw new BadRequestException("Book '" + book.getTitle() + "' is already borrowed.");
        }

        // 3. Check the member exists
        if (!memberRepository.existsById(memberId)) {
            throw new ResourceNotFoundException("Member not found with id: " + memberId);
        }

        // 4. Create the borrow record with today's date
        BorrowRecord record = new BorrowRecord();
        record.setBookId(bookId);
        record.setMemberId(memberId);
        record.setBorrowDate(LocalDate.now().toString()); // e.g. "2024-06-13"
        record.setReturnDate(null); // not returned yet

        BorrowRecord saved = borrowRepository.insert(record);

        // 5. Mark the book as unavailable
        bookRepository.updateAvailability(bookId, false);

        log.info("Book id={} borrowed by member id={}, record id={}", bookId, memberId, saved.getId());
        return saved;
    }



   
















    /**
     * Return a book.
     * - Finds the active borrow record for this book.
     * - Sets the return date to today.
     * - Marks the book as available again.
     */
    public BorrowRecord returnBook(Long bookId) {
        // 1. Check the book exists
        if (!bookRepository.existsById(bookId)) {
            throw new ResourceNotFoundException("Book not found with id: " + bookId);
        }

        // 2. Find the active borrow record (the one with no return date)
        BorrowRecord record = borrowRepository.findActiveByBookId(bookId)
                .orElseThrow(() -> new BadRequestException("This book is not currently borrowed."));

        // 3. Set today as the return date
        String today = LocalDate.now().toString();
        borrowRepository.setReturnDate(record.getId(), today);
        record.setReturnDate(today);

        // 4. Mark the book as available again
        bookRepository.updateAvailability(bookId, true);

        log.info("Book id={} returned, record id={}", bookId, record.getId());
        return record;
    }



    /**
     * Delete a borrow record.
     * - Only allowed for admin users (this check is assumed to be done in the controller).
     * - Deletes the record from the database.
     */

    public void deleteRecord(Long recordId) {
        // 1. Check the record exists
        BorrowRecord record = borrowRepository.findById(recordId)
                .orElseThrow(() -> new ResourceNotFoundException("Borrow record not found with id: " + recordId));

        // 2. Delete the record
        borrowRepository.deleteById(recordId);

        log.info("Borrow record id={} deleted", recordId);
}




    /**
     * Get a borrow record by book ID.
     * - Returns the active borrow record for the given book, if it exists.
     */
    public BorrowRecord getRecordByBookId(Long bookId) {
        // 1. Check the book exists
        if (!bookRepository.existsById(bookId)) {
            throw new ResourceNotFoundException("Book not found with id: " + bookId);
        }

        // 2. Find the active borrow record (the one with no return date)
        return borrowRepository.findActiveByBookId(bookId)
                .orElseThrow(() -> new BadRequestException("This book is not currently borrowed."));
    }
}
