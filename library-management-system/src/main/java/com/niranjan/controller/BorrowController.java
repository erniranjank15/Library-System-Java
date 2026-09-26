package com.niranjan.controller;

import com.niranjan.entity.BorrowRecord;
import com.niranjan.service.BorrowService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for borrow and return operations.
 *
 * Base URL: /api/borrow
 *
 * Endpoints:
 *   GET  /api/borrow                          -> get all borrow records (history)
 *  GET  /api/borrow/book/{bookId}             -> get borrow record for a specific book
 *   POST /api/borrow/book/{bookId}/member/{memberId}  -> borrow a book
 *   PUT  /api/borrow/return/book/{bookId}     -> return a book
 *  DELETE /api/borrow/{recordId}              -> delete a borrow record (admin only)   
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

    // GET /api/borrow/book/1  -> view borrow record for book 1
    @GetMapping("/book/{bookId}")
    public ResponseEntity<BorrowRecord> getRecordByBookId(@PathVariable Long bookId) {
        BorrowRecord record = borrowService.getRecordByBookId(bookId);
        return ResponseEntity.ok(record);
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


    // DELETE /api/borrow/{recordId}  -> delete a borrow record (admin only)
    @DeleteMapping("/{recordId}")
    public ResponseEntity<Map<String, String>> deleteRecord(@PathVariable Long recordId) {
        borrowService.deleteRecord(recordId);
        return ResponseEntity.ok(Map.of("message", "Borrow record deleted successfully"));
    }


}
