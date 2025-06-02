package com.wastewise.pickup.exception;

/**
 * Thrown when a create or delete request is invalid.
 */
public class InvalidPickUpRequestException extends RuntimeException {
    public InvalidPickUpRequestException(String message) {
        super(message);
    }
}
