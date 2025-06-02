package com.wastewise.pickup.utility;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Random;

/**
 * Utility for generating unique PickUp IDs.
 */
@Component
public class IdGenerator {

    private static final String PREFIX = "PICKUP-";
    private static final Random random = new Random();

    /**
     * Generate a unique PickUp ID using timestamp + random suffix.
     * Format: PICKUP-<epochSeconds>-<6-char alphanumeric>
     */
    public String generatePickUpId() {
        long epochSec = Instant.now().getEpochSecond();
        String randomSuffix = random.ints(48, 122 + 1) // ASCII range
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(6)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        return PREFIX + epochSec + "-" + randomSuffix;
    }
}
