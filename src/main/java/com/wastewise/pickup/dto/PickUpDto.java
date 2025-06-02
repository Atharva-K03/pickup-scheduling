package com.wastewise.pickup.dto;

import com.wastewise.pickup.model.enums.Frequency;
import com.wastewise.pickup.model.enums.PickUpStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO representing a PickUp for responses.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PickUpDto {

    private String id;
    private String zoneId;
    private LocalDateTime timeSlotStart;
    private LocalDateTime timeSlotEnd;
    private Frequency frequency;
    private String locationName;
    private String vehicleId;
    private String worker1Id;
    private String worker2Id;
    private PickUpStatus status;
}
