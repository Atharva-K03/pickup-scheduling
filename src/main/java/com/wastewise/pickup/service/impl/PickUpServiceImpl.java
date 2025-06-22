package com.wastewise.pickup.service.impl;

import com.wastewise.pickup.dto.CreatePickUpDto;
import com.wastewise.pickup.dto.DeletePickUpResponseDto;
import com.wastewise.pickup.dto.PickUpDto;
import com.wastewise.pickup.exception.InvalidPickUpRequestException;
import com.wastewise.pickup.exception.PickUpNotFoundException;
import com.wastewise.pickup.model.PickUp;
import com.wastewise.pickup.model.enums.PickUpStatus;
import com.wastewise.pickup.repository.PickUpRepository;
import com.wastewise.pickup.service.PickUpService;
import com.wastewise.pickup.utility.IdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PickUpServiceImpl implements PickUpService {

    private final PickUpRepository repository;
    private final IdGenerator idGenerator;

    private static final boolean MOCKMODE = true;

    public PickUpServiceImpl(PickUpRepository repository, IdGenerator idGenerator) {
        this.repository = repository;
        this.idGenerator = idGenerator;
    }

    @Override
    @Transactional
    public String createPickUp(CreatePickUpDto dto) {
        log.info("Received request to create PickUp: {}", dto);

        // Validate the fields in the request
        validateCreatePickUpDto(dto);

        // Generate a unique ID for the new PickUp
        String pickUpId = idGenerator.generatePickUpId();

        // Create a new PickUp object
        PickUp pickUp = new PickUp();
        pickUp.setId(pickUpId);
        pickUp.setZoneId(dto.getZoneId());
        pickUp.setTimeSlotStart(dto.getTimeSlotStart());
        pickUp.setTimeSlotEnd(dto.getTimeSlotEnd());
        pickUp.setFrequency(dto.getFrequency());
        pickUp.setLocationName(dto.getLocationName());
        pickUp.setVehicleId(dto.getVehicleId());
        pickUp.setWorker1Id(dto.getWorker1Id());
        pickUp.setWorker2Id(dto.getWorker2Id());
        pickUp.setStatus(PickUpStatus.SCHEDULED);

        // Save the new pickUp to the repository
        repository.save(pickUp);

        log.info("PickUp successfully created with ID: {}", pickUpId);
        return pickUpId;
    }

    private void validateCreatePickUpDto(CreatePickUpDto dto) {
        if (Objects.isNull(dto.getZoneId()) || dto.getZoneId().isEmpty()) {
            throw new InvalidPickUpRequestException("Zone ID is required");
        }
        if (Objects.isNull(dto.getTimeSlotStart())) {
            throw new InvalidPickUpRequestException("Time slot start is required");
        }
        if (Objects.isNull(dto.getTimeSlotEnd())) {
            throw new InvalidPickUpRequestException("Time slot end is required");
        }
        if (Objects.isNull(dto.getLocationName()) || dto.getLocationName().isEmpty()) {
            throw new InvalidPickUpRequestException("Location name is required");
        }
        // Additional validation can be added as per business requirements
    }

    @Override
    @Transactional
    public DeletePickUpResponseDto deletePickUp(String pickUpId) {
        log.info("Received request to delete PickUp with ID: {}", pickUpId);

        // Find the PickUp by its ID
        PickUp pickUp = repository.findById(pickUpId)
                .orElseThrow(() -> new PickUpNotFoundException("PickUp not found with ID: " + pickUpId));

        // Delete the PickUp
        repository.delete(pickUp);

        log.info("PickUp successfully deleted with ID: {}", pickUpId);
        return new DeletePickUpResponseDto(pickUpId, "DELETED");
    }

    @Override
    public List<PickUpDto> listAllPickUps() {
        log.info("Fetching all PickUps");

        // Fetch all PickUp records and map them to DTOs
        return repository.findAll().stream()
                .map(this::mapToPickUpDto)
                .collect(Collectors.toList());
    }

    @Override
    public PickUpDto getPickUpById(String pickUpId) {
        log.info("Fetching PickUp with ID: {}", pickUpId);

        // Find the PickUp by its ID
        PickUp pickUp = repository.findById(pickUpId)
                .orElseThrow(() -> new PickUpNotFoundException("PickUp not found with ID: " + pickUpId));

        // Map the PickUp to a DTO and return it
        return mapToPickUpDto(pickUp);
    }

    private PickUpDto mapToPickUpDto(PickUp pickUp) {
        PickUpDto dto = new PickUpDto();
        dto.setId(pickUp.getId());
        dto.setZoneId(pickUp.getZoneId());
        dto.setTimeSlotStart(pickUp.getTimeSlotStart());
        dto.setTimeSlotEnd(pickUp.getTimeSlotEnd());
        dto.setFrequency(pickUp.getFrequency());
        dto.setLocationName(pickUp.getLocationName());
        dto.setVehicleId(pickUp.getVehicleId());
        dto.setWorker1Id(pickUp.getWorker1Id());
        dto.setWorker2Id(pickUp.getWorker2Id());
        dto.setStatus(pickUp.getStatus());
        return dto;
    }
}