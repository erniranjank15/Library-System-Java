package com.niranjan.controller;

import com.niranjan.entity.Member;
import com.niranjan.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Member operations.
 *
 * Base URL: /api/members
 *
 * Endpoints:
 *   GET    /api/members        -> get all members
 *   GET    /api/members/{id}   -> get one member
 *   POST   /api/members        -> register a new member
 *   PUT    /api/members/{id}   -> update a member
 *   DELETE /api/members/{id}   -> delete a member
 */
@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    // GET /api/members
    @GetMapping
    public ResponseEntity<List<Member>> getAllMembers() {
        return ResponseEntity.ok(memberService.getAllMembers());
    }

    // GET /api/members/1
    @GetMapping("/{id}")
    public ResponseEntity<Member> getMember(@PathVariable Long id) {
        return ResponseEntity.ok(memberService.getMemberById(id));
    }

    // POST /api/members
    // Body: { "name": "Alice", "email": "alice@example.com", "phone": "9999999999" }
    @PostMapping
    public ResponseEntity<Member> addMember(@RequestBody Member member) {
        Member created = memberService.addMember(member);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // PUT /api/members/1
    @PutMapping("/{id}")
    public ResponseEntity<Member> updateMember(@PathVariable Long id, @RequestBody Member member) {
        return ResponseEntity.ok(memberService.updateMember(id, member));
    }

    // DELETE /api/members/1
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMember(@PathVariable Long id) {
        memberService.deleteMember(id);
        return ResponseEntity.ok("Member deleted successfully");
    }
}
