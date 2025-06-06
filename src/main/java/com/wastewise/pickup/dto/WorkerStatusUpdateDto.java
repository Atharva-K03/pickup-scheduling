package com.wastewise.pickup.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for worker status updates sent to Worker Service.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkerStatusUpdateDto {
    private String status; // Expected values: "OCCUPIED" or "AVAILABLE"
}