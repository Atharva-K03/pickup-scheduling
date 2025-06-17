package com.wastewise.pickup.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleDto {
    private String id;
    private String type;
    private String licensePlate;
    private String status; // AVAILABLE, OCCUPIED, MAINTENANCE
    private Double capacity;
}