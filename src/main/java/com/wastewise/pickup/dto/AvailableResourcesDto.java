package com.wastewise.pickup.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvailableResourcesDto {
    private List<ZoneDto> zones;
    private List<WorkerDto> workers;
    private List<VehicleDto> vehicles;
}