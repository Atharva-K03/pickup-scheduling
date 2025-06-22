package com.wastewise.pickup.utility;

import com.wastewise.pickup.model.PickUp;
import com.wastewise.pickup.repository.PickUpRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Utility for generating unique PickUp IDs.
 */
@Component
@RequiredArgsConstructor
public class IdGenerator {

    private final PickUpRepository pickupRepository;
    private static final String PREFIX = "P";
    private static final AtomicInteger counter = new AtomicInteger(0);

    /**
     * Generate a unique PickUp ID using an incrementing counter.
     * Format: PXXX
     */
    public String generatePickUpId() {
        Optional<PickUp> op = pickupRepository.findFirstByOrderByIdDesc();
        if(op.isPresent()) {
            String lastId = op.get().getId();
            if (lastId.startsWith(PREFIX)) {
                String numberPart = lastId.substring(PREFIX.length());

                try {
                    int lastNumber = Integer.parseInt(numberPart);
                    counter.set(lastNumber);
                } catch (NumberFormatException e) {
                    // If parsing fails, reset counter to 0
                    counter.set(0);
                }
            }
        }
        int id = counter.incrementAndGet();
        return PREFIX + String.format("%03d", id);
    }
}