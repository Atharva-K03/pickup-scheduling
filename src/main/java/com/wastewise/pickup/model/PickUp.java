package com.wastewise.pickup.model;

import com.wastewise.pickup.model.enums.Frequency;
import com.wastewise.pickup.model.enums.PickUpStatus;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

/**
 * Entity representing an ad-hoc waste pickup assignment.
 */
@Entity
@Table(name = "pickups")

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Slf4j
public class PickUp {

    @Id
    private String id;

    private String zoneId;

    private LocalDateTime timeSlotStart;

    private LocalDateTime timeSlotEnd;

    @Enumerated(EnumType.STRING)
    private Frequency frequency;

    private String locationName;

    private String vehicleId;

    private String worker1Id;

    private String worker2Id;

    @Enumerated(EnumType.STRING)
    private PickUpStatus status;

    /**
     * Log before persisting a new pickup.
     */
    @PrePersist
    public void prePersist() {
        log.info("About to persist new PickUp with ID: {}", this.id);
    }

    /**
     * Log before removing a pickup.
     */
    @PreRemove
    public void preRemove() {
        log.info("About to remove PickUp with ID: {}", this.id);
    }
}