package com.wastewise.pickup.service.impl;

import com.wastewise.pickup.dto.CreatePickUpDto;
import com.wastewise.pickup.dto.DeletePickUpResponseDto;
import com.wastewise.pickup.dto.PickUpDto;
import com.wastewise.pickup.model.enums.PickUpStatus;
import com.wastewise.pickup.exception.InvalidPickUpRequestException;
import com.wastewise.pickup.exception.PickUpNotFoundException;
import com.wastewise.pickup.model.PickUp;
import com.wastewise.pickup.repository.PickUpRepository;
import com.wastewise.pickup.service.PickUpService;
import com.wastewise.pickup.utility.IdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Slf4j
@Service
public class PickUpServiceImpl implements PickUpService {

    private final PickUpRepository repository;
    private final IdGenerator idGenerator;

    // Enable mock mode for local testing
    private static final boolean mockMode = true;

    public PickUpServiceImpl(PickUpRepository repository, IdGenerator idGenerator) {
        this.repository = repository;
        this.idGenerator = idGenerator;
    }

    @Override
    @Transactional
    public String createPickUp(CreatePickUpDto dto) {
        log.info("Received request to create PickUp: {}", dto);

        if (dto.getTimeSlotEnd().isBefore(dto.getTimeSlotStart().plusMinutes(30))) {
            throw new InvalidPickUpRequestException("End time must be at least 30 minutes after start time.");
        }

        // --- Mock zone check ---
        if (mockMode) {
            log.debug("MOCK: Zone {} validated", dto.getZoneId());
        } else {
            throw new UnsupportedOperationException("Zone validation requires remote service.");
        }

        // --- Mock available vehicle ---
        if (mockMode) {
            log.debug("MOCK: Vehicle {} validated", dto.getVehicleId());
        } else {
            throw new UnsupportedOperationException("Vehicle validation requires remote service.");
        }

        // --- Mock available workers ---
        if (mockMode) {
            if (dto.getWorker1Id().equals(dto.getWorker2Id())) {
                throw new InvalidPickUpRequestException("Worker 1 and Worker 2 cannot be the same.");
            }
            log.debug("MOCK: Workers {} and {} validated", dto.getWorker1Id(), dto.getWorker2Id());
        } else {
            throw new UnsupportedOperationException("Worker validation requires remote service.");
        }

        // --- Save pickup ---
        String newId = idGenerator.generatePickUpId();
        PickUp pickUp = PickUp.builder()
                .id(newId)
                .zoneId(dto.getZoneId())
                .timeSlotStart(dto.getTimeSlotStart())
                .timeSlotEnd(dto.getTimeSlotEnd())
                .frequency(dto.getFrequency())
                .locationName(dto.getLocationName())
                .vehicleId(dto.getVehicleId())
                .worker1Id(dto.getWorker1Id())
                .worker2Id(dto.getWorker2Id())
                .status(PickUpStatus.NOT_STARTED)
                .build();
        repository.save(pickUp);
        log.info("Persisted PickUp with ID: {}", newId);

        // --- Mock logging & occupancy ---
        if (mockMode) {
            log.debug("MOCK: Logged creation event and marked resources occupied.");
        }

        return newId;
    }

    @Override
    @Transactional
    public DeletePickUpResponseDto deletePickUp(String pickUpId) {
        log.info("Received request to delete PickUp ID: {}", pickUpId);

        PickUp existing = repository.findById(pickUpId)
                .orElseThrow(() -> new PickUpNotFoundException(pickUpId));

        repository.delete(existing);
        log.info("Deleted PickUp ID: {}", pickUpId);

        if (mockMode) {
            log.debug("MOCK: Freed vehicle and workers, and logged deletion.");
        }

        return new DeletePickUpResponseDto(pickUpId, "DELETED");
    }

    @Override
    public List<PickUpDto> listAllPickUps() {
        log.info("Listing all PickUps");
        List<PickUp> all = repository.findAll();
        log.debug("Found {} pickups", all.size());
        return all.stream()
                .map(p -> PickUpDto.builder()
                        .id(p.getId())
                        .zoneId(p.getZoneId())
                        .timeSlotStart(p.getTimeSlotStart())
                        .timeSlotEnd(p.getTimeSlotEnd())
                        .frequency(p.getFrequency())
                        .locationName(p.getLocationName())
                        .vehicleId(p.getVehicleId())
                        .worker1Id(p.getWorker1Id())
                        .worker2Id(p.getWorker2Id())
                        .status(p.getStatus())
                        .build())
                        .toList();
    }

    @Override
    public PickUpDto getPickUpById(String pickUpId) {
        log.info("Fetching PickUp by ID: {}", pickUpId);
        PickUp p = repository.findById(pickUpId)
                .orElseThrow(() -> new PickUpNotFoundException(pickUpId));
        return PickUpDto.builder()
                .id(p.getId())
                .zoneId(p.getZoneId())
                .timeSlotStart(p.getTimeSlotStart())
                .timeSlotEnd(p.getTimeSlotEnd())
                .frequency(p.getFrequency())
                .locationName(p.getLocationName())
                .vehicleId(p.getVehicleId())
                .worker1Id(p.getWorker1Id())
                .worker2Id(p.getWorker2Id())
                .status(p.getStatus())
                .build();
    }

}
