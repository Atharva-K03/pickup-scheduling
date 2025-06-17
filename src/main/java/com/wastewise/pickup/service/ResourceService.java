package com.wastewise.pickup.service;

import com.wastewise.pickup.dto.AvailableResourcesDto;
import com.wastewise.pickup.dto.VehicleDto;
import com.wastewise.pickup.dto.WorkerDto;
import com.wastewise.pickup.dto.ZoneDto;

import java.util.List;

/**
 * Service interface for resource operations.
 */
public interface ResourceService {

    /**
     * Get all available resources for the frontend.
     * @return DTO containing zones, workers, and vehicles.
     */
    AvailableResourcesDto getAllAvailableResources();

    /**
     * Get all zones.
     * @return list of ZoneDto.
     */
    List<ZoneDto> getAllZones();

    /**
     * Get all workers.
     * @return list of WorkerDto.
     */
    List<WorkerDto> getAllWorkers();

    /**
     * Get all vehicles.
     * @return list of VehicleDto.
     */
    List<VehicleDto> getAllVehicles();
}