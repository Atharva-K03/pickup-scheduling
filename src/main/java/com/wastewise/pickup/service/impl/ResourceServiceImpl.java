package com.wastewise.pickup.service.impl;

import com.wastewise.pickup.client.VehicleServiceClient;
import com.wastewise.pickup.client.WorkerServiceClient;
import com.wastewise.pickup.client.ZoneServiceClient;
import com.wastewise.pickup.dto.AvailableResourcesDto;
import com.wastewise.pickup.dto.VehicleDto;
import com.wastewise.pickup.dto.WorkerDto;
import com.wastewise.pickup.dto.ZoneDto;
import com.wastewise.pickup.exception.ResourceNotAvailableException;
import com.wastewise.pickup.exception.ServiceCommunicationException;
import com.wastewise.pickup.service.ResourceService;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {

    private final ZoneServiceClient zoneServiceClient;
    private final WorkerServiceClient workerServiceClient;
    private final VehicleServiceClient vehicleServiceClient;

    @Override
    public AvailableResourcesDto getAllAvailableResources() {
        log.info("Fetching all available resources");

        // Use CompletableFuture to make parallel requests to all services
        CompletableFuture<List<ZoneDto>> zonesFuture = CompletableFuture.supplyAsync(this::getAllZones);
        CompletableFuture<List<WorkerDto>> workersFuture = CompletableFuture.supplyAsync(this::getAllWorkers);
        CompletableFuture<List<VehicleDto>> vehiclesFuture = CompletableFuture.supplyAsync(this::getAllVehicles);

        try {
            // Wait for all futures to complete
            CompletableFuture.allOf(zonesFuture, workersFuture, vehiclesFuture).join();

            // Retrieve the results
            List<ZoneDto> zones = zonesFuture.get();
            List<WorkerDto> workers = workersFuture.get();
            List<VehicleDto> vehicles = vehiclesFuture.get();

            log.info("Successfully fetched all resources: {} zones, {} workers, {} vehicles",
                    zones.size(), workers.size(), vehicles.size());

            return new AvailableResourcesDto(zones, workers, vehicles);
        } catch (Exception e) {
            log.error("Failed to fetch all resources: {}", e.getMessage());
            throw new ServiceCommunicationException("Failed to fetch all resources: " + e.getMessage());
        }
    }

    @Override
    @CircuitBreaker(name = "getZones", fallbackMethod = "getZonesFallback")
    public List<ZoneDto> getAllZones() {
        try {
            List<ZoneDto> zones = zoneServiceClient.getAllZones();

            if (zones == null || zones.isEmpty()) {
                log.error("No zones available from zone service");
                throw new ResourceNotAvailableException("No zones available");
            }

            log.info("Successfully fetched {} zones", zones.size());
            return zones;
        } catch (FeignException e) {
            log.error("Failed to fetch zones: {}", e.getMessage());
            throw new ServiceCommunicationException("Failed to fetch zones: " + e.getMessage());
        }
    }

    private List<ZoneDto> getZonesFallback(Exception e) {
        log.warn("Using fallback for zones due to: {}", e.getMessage());
        // In production, you might use a cached list or basic zones
        return List.of();
    }

    @Override
    @CircuitBreaker(name = "getWorkers", fallbackMethod = "getWorkersFallback")
    public List<WorkerDto> getAllWorkers() {
        try {
            List<WorkerDto> workers = workerServiceClient.getAllWorkers();

            if (workers == null || workers.isEmpty()) {
                log.error("No workers available from worker service");
                throw new ResourceNotAvailableException("No workers available");
            }

            if (workers.size() < 2) {
                log.warn("Only {} workers available. At least 2 are recommended.", workers.size());
            }

            // Only return available workers for selection
            List<WorkerDto> availableWorkers = workers.stream()
                    .filter(worker -> "AVAILABLE".equals(worker.getStatus()))
                    .toList();

            log.info("Successfully fetched {} workers ({} available)",
                    workers.size(), availableWorkers.size());

            return availableWorkers;
        } catch (FeignException e) {
            log.error("Failed to fetch workers: {}", e.getMessage());
            throw new ServiceCommunicationException("Failed to fetch workers: " + e.getMessage());
        }
    }

    private List<WorkerDto> getWorkersFallback(Exception e) {
        log.warn("Using fallback for workers due to: {}", e.getMessage());
        // In production, you might use a cached list or basic workers
        return List.of();
    }

    @Override
    @CircuitBreaker(name = "getVehicles", fallbackMethod = "getVehiclesFallback")
    public List<VehicleDto> getAllVehicles() {
        try {
            List<VehicleDto> vehicles = vehicleServiceClient.getAllVehicles();

            if (vehicles == null || vehicles.isEmpty()) {
                log.error("No vehicles available from vehicle service");
                throw new ResourceNotAvailableException("No vehicles available");
            }

            // Only return available vehicles for selection
            List<VehicleDto> availableVehicles = vehicles.stream()
                    .filter(vehicle -> "AVAILABLE".equals(vehicle.getStatus()))
                    .toList();

            log.info("Successfully fetched {} vehicles ({} available)",
                    vehicles.size(), availableVehicles.size());

            return availableVehicles;
        } catch (FeignException e) {
            log.error("Failed to fetch vehicles: {}", e.getMessage());
            throw new ServiceCommunicationException("Failed to fetch vehicles: " + e.getMessage());
        }
    }

    private List<VehicleDto> getVehiclesFallback(Exception e) {
        log.warn("Using fallback for vehicles due to: {}", e.getMessage());
        // In production, you might use a cached list or basic vehicles
        return List.of();
    }
}