package com.wastewise.pickup.controller;

import com.wastewise.pickup.dto.AvailableResourcesDto;
import com.wastewise.pickup.dto.VehicleDto;
import com.wastewise.pickup.dto.WorkerDto;
import com.wastewise.pickup.dto.ZoneDto;
import com.wastewise.pickup.service.ResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for accessing resources from other microservices.
 */
@RestController
@RequestMapping("/wastewise/scheduler/resources")
@RequiredArgsConstructor
@Slf4j
public class ResourceController {

    private final ResourceService resourceService;

    /**
     * Retrieves all available zones, workers, and vehicles for the frontend.
     *
     * @return a ResponseEntity containing all available resources and HTTP status 200 (OK)
     */
    @GetMapping
    public ResponseEntity<AvailableResourcesDto> getAllResources() {
        log.info("GET /wastewise/scheduler/resources");
        AvailableResourcesDto resources = resourceService.getAllAvailableResources();
        log.debug("Returning resources: {} zones, {} workers, {} vehicles",
                resources.getZones().size(), resources.getWorkers().size(), resources.getVehicles().size());
        return ResponseEntity.ok(resources);
    }

    /**
     * Retrieves all available zones.
     *
     * @return a ResponseEntity containing all available zones and HTTP status 200 (OK)
     */
    @GetMapping("/zones")
    public ResponseEntity<List<ZoneDto>> getAllZones() {
        log.info("GET /wastewise/scheduler/resources/zones");
        List<ZoneDto> zones = resourceService.getAllZones();
        log.debug("Returning {} zones", zones.size());
        return ResponseEntity.ok(zones);
    }

    /**
     * Retrieves all available workers.
     *
     * @return a ResponseEntity containing all available workers and HTTP status 200 (OK)
     */
    @GetMapping("/workers")
    public ResponseEntity<List<WorkerDto>> getAllWorkers() {
        log.info("GET /wastewise/scheduler/resources/workers");
        List<WorkerDto> workers = resourceService.getAllWorkers();
        log.debug("Returning {} workers", workers.size());
        return ResponseEntity.ok(workers);
    }

    /**
     * Retrieves all available vehicles.
     *
     * @return a ResponseEntity containing all available vehicles and HTTP status 200 (OK)
     */
    @GetMapping("/vehicles")
    public ResponseEntity<List<VehicleDto>> getAllVehicles() {
        log.info("GET /wastewise/scheduler/resources/vehicles");
        List<VehicleDto> vehicles = resourceService.getAllVehicles();
        log.debug("Returning {} vehicles", vehicles.size());
        return ResponseEntity.ok(vehicles);
    }
}