package com.wastewise.pickup.exception;

/**
 * Thrown when a PickUp with given ID does not exist.
 */
public class PickUpNotFoundException extends RuntimeException {
    public PickUpNotFoundException(String pickUpId) {

        super("PickUp not found with ID: " + pickUpId);
    }
}
