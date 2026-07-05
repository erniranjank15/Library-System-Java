package com.niranjan.exception;

/**
 * Thrown when the request is invalid — e.g. trying to borrow a book that is already borrowed.
 */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
