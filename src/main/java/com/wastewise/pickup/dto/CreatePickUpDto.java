package com.wastewise.pickup.dto;

import com.wastewise.pickup.model.enums.Frequency;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for creating a new PickUp.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePickUpDto {

    @NotBlank(message = "zoneId must not be blank")
    private String zoneId;

    @NotNull(message = "timeSlotStart must be provided")
    @Future(message = "timeSlotStart must be in the future")
    private LocalDateTime timeSlotStart;

    @NotNull(message = "timeSlotEnd must be provided")
    @Future(message = "timeSlotEnd must be in the future")
    private LocalDateTime timeSlotEnd;

    @NotNull(message = "frequency must be provided")
    private Frequency frequency;

    @NotBlank(message = "locationName must not be blank")
    private String locationName;

    @NotBlank(message = "vehicleId must not be blank")
    private String vehicleId;

    @NotBlank(message = "worker1Id must not be blank")
    private String worker1Id;

    @NotBlank(message = "worker2Id must not be blank")
    private String worker2Id;

    public List<String> getWorkerIds() {
        return List.of(worker1Id, worker2Id);
    }

}
