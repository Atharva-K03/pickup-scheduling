package com.wastewise.pickup.utility;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Utility for generating unique PickUp IDs.
 */
@Component
public class IdGenerator {

    private static final String PREFIX = "P";
    private static final AtomicInteger counter = new AtomicInteger(0);

    /**
     * Generate a unique PickUp ID using an incrementing counter.
     * Format: PXXX
     */
    public String generatePickUpId() {
        int id = counter.incrementAndGet();
        return PREFIX + String.format("%03d", id);
    }
}
