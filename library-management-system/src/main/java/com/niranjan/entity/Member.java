package com.niranjan.entity;

import jakarta.persistence.*;

/**
 * Represents a library member (a person who can borrow books).
 * Mapped to 'members' table in PostgreSQL via JPA / Hibernate.
 */
@Entity
@Table(name = "members")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;   // used as a unique contact identifier

    @Column(length = 50)
    private String phone;

    public Member() {}

    public Member(Long id, String name, String email, String phone) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    // --- Getters and Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    @Override
    public String toString() {
        return "Member{id=" + id + ", name='" + name + "', email='" + email + "'}";
    }
}
