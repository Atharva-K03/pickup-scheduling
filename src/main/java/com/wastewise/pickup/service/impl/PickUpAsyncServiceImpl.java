package com.wastewise.pickup.service.impl;

import com.wastewise.pickup.client.VehicleServiceClient;
import com.wastewise.pickup.client.WorkerServiceClient;
import com.wastewise.pickup.dto.VehicleStatusUpdateDto;
import com.wastewise.pickup.dto.WorkerStatusUpdateDto;
import com.wastewise.pickup.service.PickUpAsyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class PickUpAsyncServiceImpl implements PickUpAsyncService {

    private final WorkerServiceClient workerServiceClient;
    private final VehicleServiceClient vehicleServiceClient;

    private static final String STATUS_OCCUPIED = "OCCUPIED";
    private static final String STATUS_AVAILABLE = "AVAILABLE";

    /**
     * Updates the status of resources (vehicle and workers) to "OCCUPIED".
     */
    @Async
    @Override
    public void updateResourceStatus(String worker1Id, String worker2Id, String vehicleId) {
        log.info("Updating resources to '{}' status: Worker1 ID: {}, Worker2 ID: {}, Vehicle ID: {}",
                STATUS_OCCUPIED, worker1Id, worker2Id, vehicleId);

        CompletableFuture.allOf(
                updateWorkerStatus(worker1Id, STATUS_OCCUPIED),
                updateWorkerStatus(worker2Id, STATUS_OCCUPIED),
                updateVehicleStatus(vehicleId, STATUS_OCCUPIED)
        ).join();

        log.info("Resources updated to '{}' status successfully.", STATUS_OCCUPIED);
    }

    /**
     * Releases all resources by updating their statuses to "AVAILABLE".
     */
    @Async
    @Override
    public void releaseResources(String worker1Id, String worker2Id, String vehicleId) {
        log.info("Releasing resources to '{}' status: Worker1 ID: {}, Worker2 ID: {}, Vehicle ID: {}",
                STATUS_AVAILABLE, worker1Id, worker2Id, vehicleId);

        CompletableFuture.allOf(
                updateWorkerStatus(worker1Id, STATUS_AVAILABLE),
                updateWorkerStatus(worker2Id, STATUS_AVAILABLE),
                updateVehicleStatus(vehicleId, STATUS_AVAILABLE)
        ).join();

        log.info("Resources released to '{}' status successfully.", STATUS_AVAILABLE);
    }

    /**
     * Updates the worker status asynchronously.
     */
    @Async
    @Override
    public CompletableFuture<Void> updateWorkerStatus(String workerId, String status) {
        return CompletableFuture.runAsync(() -> {
            WorkerStatusUpdateDto updateDto = new WorkerStatusUpdateDto(workerId, status);
            workerServiceClient.updateWorkerStatus(updateDto);
            log.info("Updated worker ID '{}' to status '{}'.", workerId, status);
        });
    }

    /**
     * Updates the vehicle status asynchronously.
     */
    @Async
    @Override
    public CompletableFuture<Void> updateVehicleStatus(String vehicleId, String status) {
        return CompletableFuture.runAsync(() -> {
            VehicleStatusUpdateDto updateDto = new VehicleStatusUpdateDto(vehicleId, status);
            vehicleServiceClient.updateVehicleStatus(updateDto);
            log.info("Updated vehicle ID '{}' to status '{}'.", vehicleId, status);
        });
    }
}