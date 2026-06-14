package com.niranjan.exception;

/**
 * Thrown when a requested resource (book, member, etc.) does not exist in the database.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
