package com.niranjan.repository;

import com.niranjan.entity.BorrowRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository for BorrowRecord entity.
 * Provides all CRUD operations automatically without writing any raw SQL queries.
 */
@Repository
public interface BorrowRepository extends JpaRepository<BorrowRecord, Long> {

    Optional<BorrowRecord> findByBookIdAndReturnDateIsNull(Long bookId);

    Optional<BorrowRecord> findByMemberIdAndReturnDateIsNull(Long memberId);

    List<BorrowRecord> findByBookId(Long bookId);

    List<BorrowRecord> findByMemberId(Long memberId);

    void deleteByBookId(Long bookId);

    void deleteByMemberId(Long memberId);

    default Optional<BorrowRecord> findActiveByBookId(Long bookId) {
        return findByBookIdAndReturnDateIsNull(bookId);
    }

    default Optional<BorrowRecord> findActiveByMemberId(Long memberId) {
        return findByMemberIdAndReturnDateIsNull(memberId);
    }

    default List<BorrowRecord> findAllByBookId(Long bookId) {
        return findByBookId(bookId);
    }

    default List<BorrowRecord> findAllByMemberId(Long memberId) {
        return findByMemberId(memberId);
    }

    default BorrowRecord insert(BorrowRecord record) {
        return save(record);
    }

    default void setReturnDate(Long recordId, String returnDate) {
        findById(recordId).ifPresent(record -> {
            record.setReturnDate(returnDate);
            save(record);
        });
    }
}