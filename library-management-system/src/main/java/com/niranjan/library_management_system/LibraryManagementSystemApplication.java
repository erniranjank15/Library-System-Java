package com.niranjan.library_management_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Entry point for the Library Management System.
 */
@SpringBootApplication(scanBasePackages = "com.niranjan")
@EnableJpaRepositories(basePackages = "com.niranjan.repository")
@EntityScan(basePackages = "com.niranjan.entity")
public class LibraryManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibraryManagementSystemApplication.class, args);
    }
}
