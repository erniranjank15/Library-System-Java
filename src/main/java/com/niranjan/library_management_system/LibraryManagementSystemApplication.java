package com.niranjan.library_management_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Library Management System.
 *
 * @SpringBootApplication enables:
 *   - Auto-configuration (Spring figures out beans automatically)
 *   - Component scanning (finds @RestController, @Service, @Repository, etc.)
 */
@SpringBootApplication(scanBasePackages = "com.niranjan")
public class LibraryManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibraryManagementSystemApplication.class, args);
    }
}
