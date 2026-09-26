package com.niranjan.repository;

import com.niranjan.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA Repository for Member entity.
 * Provides all CRUD operations automatically without writing any raw SQL queries.
 */
@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    // Convenience aliases for existing service method compatibility
    default Member insert(Member member) {
        return save(member);
    }

    default Member update(Member member) {
        return save(member);
    }
}
