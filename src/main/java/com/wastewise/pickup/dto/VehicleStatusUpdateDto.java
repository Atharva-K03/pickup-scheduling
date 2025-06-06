package com.wastewise.pickup.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for vehicle status updates sent to Vehicle Service.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleStatusUpdateDto {
    private String vehicleId; // ID of the vehicle (e.g., "PT001")
    private String status;    // Expected values: "OCCUPIED" or "AVAILABLE"
}