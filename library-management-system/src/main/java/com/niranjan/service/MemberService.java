package com.niranjan.service;

import com.niranjan.entity.Member;
import com.niranjan.exception.BadRequestException;
import com.niranjan.exception.ResourceNotFoundException;
import com.niranjan.repository.BorrowRepository;
import com.niranjan.repository.MemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Contains all the business logic for members.
 */
@Service
public class MemberService {

    private static final Logger log = LoggerFactory.getLogger(MemberService.class);

    private final MemberRepository memberRepository;
    private final BorrowRepository borrowRepository;

    public MemberService(MemberRepository memberRepository, BorrowRepository borrowRepository) {
        this.memberRepository = memberRepository;
        this.borrowRepository = borrowRepository;
    }

    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    public Member getMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + id));
    }

    public Member addMember(Member member) {
        Member saved = memberRepository.insert(member);
        log.info("Added new member: id={}, name={}", saved.getId(), saved.getName());
        return saved;
    }

    public Member updateMember(Long id, Member member) {
        getMemberById(id); // will throw 404 if not found
        member.setId(id);
        Member updated = memberRepository.update(member);
        log.info("Updated member id={}", id);
        return updated;
    }

    public void deleteMember(Long id) {
        // Make sure the member exists before deleting
        Member member = getMemberById(id);

        // Check if member has any active borrow records (unreturned books)
        borrowRepository.findActiveByMemberId(id).ifPresent(record -> {
            throw new BadRequestException("Cannot delete member '" + member.getName() + "' because they have an unreturned book.");
        });

        // Delete all borrow records (history) for this member
        borrowRepository.deleteById(id);

        // Now delete the member
        memberRepository.deleteById(id);
        log.info("Deleted member id={}", id);
    }
}
