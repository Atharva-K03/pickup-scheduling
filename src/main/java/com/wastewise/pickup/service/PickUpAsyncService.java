package com.wastewise.pickup.service;

import java.util.concurrent.CompletableFuture;

public interface PickUpAsyncService {
    void updateResourceStatus(String worker1Id, String worker2Id, String vehicleId);
    void releaseResources(String worker1Id, String worker2Id, String vehicleId);
    CompletableFuture<Void> updateWorkerStatus(String workerId, String status);
    CompletableFuture<Void> updateVehicleStatus(String vehicleId, String status);
}