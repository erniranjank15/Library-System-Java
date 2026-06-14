package com.niranjan.controller;

import com.niranjan.entity.BorrowRecord;
import com.niranjan.service.BorrowService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for borrow and return operations.
 *
 * Base URL: /api/borrow
 *
 * Endpoints:
 *   GET  /api/borrow                          -> get all borrow records (history)
 *   POST /api/borrow/book/{bookId}/member/{memberId}  -> borrow a book
 *   PUT  /api/borrow/return/book/{bookId}     -> return a book
 */
@RestController
@RequestMapping("/api/borrow")
public class BorrowController {

    private final BorrowService borrowService;

    public BorrowController(BorrowService borrowService) {
        this.borrowService = borrowService;
    }

    // GET /api/borrow  -> view full borrow history
    @GetMapping
    public ResponseEntity<List<BorrowRecord>> getAllRecords() {
        return ResponseEntity.ok(borrowService.getAllRecords());
    }

    // POST /api/borrow/book/1/member/2  -> member 2 borrows book 1
    @PostMapping("/book/{bookId}/member/{memberId}")
    public ResponseEntity<BorrowRecord> borrowBook(
            @PathVariable Long bookId,
            @PathVariable Long memberId) {
        BorrowRecord record = borrowService.borrowBook(bookId, memberId);
        return ResponseEntity.ok(record);
    }

    // PUT /api/borrow/return/book/1  -> return book 1
    @PutMapping("/return/book/{bookId}")
    public ResponseEntity<BorrowRecord> returnBook(@PathVariable Long bookId) {
        BorrowRecord record = borrowService.returnBook(bookId);
        return ResponseEntity.ok(record);
    }
}
