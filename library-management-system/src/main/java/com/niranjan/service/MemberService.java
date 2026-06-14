package com.niranjan.service;

import com.niranjan.entity.Member;
import com.niranjan.exception.ResourceNotFoundException;
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

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
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
        getMemberById(id); // will throw 404 if not found
        memberRepository.deleteById(id);
        log.info("Deleted member id={}", id);
    }
}
