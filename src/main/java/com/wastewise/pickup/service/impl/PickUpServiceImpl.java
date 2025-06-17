package com.wastewise.pickup.service.impl;

import com.wastewise.pickup.client.VehicleServiceClient;
import com.wastewise.pickup.client.WorkerServiceClient;
import com.wastewise.pickup.client.ZoneServiceClient;
import com.wastewise.pickup.dto.CreatePickUpDto;
import com.wastewise.pickup.dto.DeletePickUpResponseDto;
import com.wastewise.pickup.dto.PickUpDto;
import com.wastewise.pickup.exception.PickUpNotFoundException;
import com.wastewise.pickup.model.PickUp;
import com.wastewise.pickup.model.enums.PickUpStatus;
import com.wastewise.pickup.repository.PickUpRepository;
import com.wastewise.pickup.service.PickUpAsyncService;
import com.wastewise.pickup.service.PickUpService;
import com.wastewise.pickup.utility.IdGenerator;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PickUpServiceImpl implements PickUpService {

    private final PickUpRepository repository;
    private final IdGenerator idGenerator;
    private final ZoneServiceClient zoneServiceClient;
    private final WorkerServiceClient workerServiceClient;
    private final VehicleServiceClient vehicleServiceClient;
    private final PickUpAsyncService asyncService;

    @Override
    @Transactional
    public String createPickUp(CreatePickUpDto dto) {
        log.info("Received createPickUp request: {}", dto);

        // Validate zone, workers, and vehicle
        validateZone(dto.getZoneId());
        validateWorkers(dto.getWorker1Id(), dto.getWorker2Id());
        validateVehicle(dto.getVehicleId());

        // Persist new pick-up
        String generatedId = idGenerator.generatePickUpId();
        PickUp pickUp = PickUp.builder()
                .id(generatedId)
                .zoneId(dto.getZoneId())
                .timeSlotStart(dto.getTimeSlotStart())
                .timeSlotEnd(dto.getTimeSlotEnd())
                .frequency(dto.getFrequency())
                .locationName(dto.getLocationName())
                .vehicleId(dto.getVehicleId())
                .worker1Id(dto.getWorker1Id())
                .worker2Id(dto.getWorker2Id())
                .status(PickUpStatus.SCHEDULED)
                .build();

        repository.save(pickUp);
        log.info("PickUp with ID '{}' created successfully.", generatedId);

        // Trigger async resource updates
        asyncService.updateResourceStatus(dto.getWorker1Id(), dto.getWorker2Id(), dto.getVehicleId());

        return generatedId;
    }

    @Override
    public List<PickUpDto> listAllPickUps() {
        return repository.findAll().stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public PickUpDto getPickUpById(String pickUpId) {
        return repository.findById(pickUpId)
                .map(this::mapToDto)
                .orElseThrow(() -> new PickUpNotFoundException(pickUpId));
    }

    @Override
    @Transactional
    public DeletePickUpResponseDto deletePickUp(String pickUpId) {
        log.info("Deleting PickUp with ID '{}'.", pickUpId);

        PickUp existingPickUp = repository.findById(pickUpId)
                .orElseThrow(() -> new PickUpNotFoundException(pickUpId));

        repository.delete(existingPickUp);
        log.info("PickUp with ID '{}' deleted successfully.", pickUpId);

        // Trigger async resource release updates
        asyncService.releaseResources(existingPickUp.getWorker1Id(), existingPickUp.getWorker2Id(), existingPickUp.getVehicleId());

        return DeletePickUpResponseDto.builder()
                .pickUpId(pickUpId)
                .status("PickUp deleted successfully.")
                .build();
    }

    private void validateZone(String zoneId) {
        if (zoneServiceClient.getAllZones().stream().noneMatch(zone -> zone.getId().equals(zoneId))) {
            throw new IllegalArgumentException("Invalid or unavailable zone: " + zoneId);
        }
    }

    private void validateWorkers(String worker1Id, String worker2Id) {
        if (workerServiceClient.getWorkerById(worker1Id) == null || workerServiceClient.getWorkerById(worker2Id) == null) {
            throw new IllegalArgumentException("Invalid or unavailable workers: " + worker1Id + ", " + worker2Id);
        }
    }

    private void validateVehicle(String vehicleId) {
        if (vehicleServiceClient.getVehicleById(vehicleId) == null) {
            throw new IllegalArgumentException("Invalid or unavailable vehicle: " + vehicleId);
        }
    }

    private PickUpDto mapToDto(PickUp pickUp) {
        return PickUpDto.builder()
                .id(pickUp.getId())
                .zoneId(pickUp.getZoneId())
                .timeSlotStart(pickUp.getTimeSlotStart())
                .timeSlotEnd(pickUp.getTimeSlotEnd())
                .frequency(pickUp.getFrequency())
                .locationName(pickUp.getLocationName())
                .vehicleId(pickUp.getVehicleId())
                .worker1Id(pickUp.getWorker1Id())
                .worker2Id(pickUp.getWorker2Id())
                .status(pickUp.getStatus())
                .build();
    }
}