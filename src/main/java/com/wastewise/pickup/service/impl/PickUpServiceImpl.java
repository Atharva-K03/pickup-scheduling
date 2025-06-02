// File: src/main/java/com/wastewise/pickup/service/impl/PickUpServiceImpl.java
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
import lombok.AllArgsConstructor;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of PickUpService.
 */
@Service
public class PickUpServiceImpl implements PickUpService {

    private static final Logger logger = LoggerFactory.getLogger(PickUpServiceImpl.class);

    private final PickUpRepository repository;
    private final IdGenerator idGenerator;
    private final WebClient webClient;
    private final String zoneServiceUrl;
    private final String vehicleServiceUrl;
    private final String workerServiceUrl;
    private final String loggingServiceUrl;

    public PickUpServiceImpl(
            PickUpRepository repository,
            IdGenerator idGenerator,
            WebClient.Builder webClientBuilder,
            @Value("${zone-service.url}") String zoneServiceUrl,
            @Value("${vehicle-service.url}") String vehicleServiceUrl,
            @Value("${worker-service.url}") String workerServiceUrl,
            @Value("${logging-service.url}") String loggingServiceUrl) {
        this.repository = repository;
        this.idGenerator = idGenerator;
        this.webClient = webClientBuilder.build();
        this.zoneServiceUrl = zoneServiceUrl;
        this.vehicleServiceUrl = vehicleServiceUrl;
        this.workerServiceUrl = workerServiceUrl;
        this.loggingServiceUrl = loggingServiceUrl;
    }

    @Override
    @Transactional
    public String createPickUp(CreatePickUpDto dto) {
        logger.info("Received request to create PickUp: {}", dto);

        // 1. Validate time window
        if (dto.getTimeSlotEnd().isBefore(dto.getTimeSlotStart().plusMinutes(30))) {
            throw new InvalidPickUpRequestException("End time must be at least 30 minutes after start time.");
        }

        // 2. Verify zone exists
        Boolean zoneExists = webClient.get()
                .uri(zoneServiceUrl + "/api/zones/{zoneId}", dto.getZoneId())
                .retrieve()
                .bodyToMono(Boolean.class)
                .block(Duration.ofSeconds(5));
        if (zoneExists == null || !zoneExists) {
            throw new InvalidPickUpRequestException("Zone not found: " + dto.getZoneId());
        }
        logger.debug("Zone {} validated", dto.getZoneId());

        // 3. Calculate buffer window
        var bufferStart = dto.getTimeSlotStart().minusMinutes(30);
        var bufferEnd = dto.getTimeSlotEnd().plusMinutes(30);

        // 4. Fetch available vehicles
        List<String> availableVehicles = webClient.get()
                .uri(vehicleServiceUrl + "/api/vehicles/available?zoneId={zoneId}&start={start}&end={end}",
                        dto.getZoneId(), dto.getTimeSlotStart(), dto.getTimeSlotEnd())
                .retrieve()
                .bodyToFlux(String.class)
                .collectList()
                .block(Duration.ofSeconds(5));
        if (availableVehicles == null || !availableVehicles.contains(dto.getVehicleId())) {
            throw new InvalidPickUpRequestException("Vehicle not available: " + dto.getVehicleId());
        }
        logger.debug("Vehicle {} validated", dto.getVehicleId());

        // 5. Fetch available workers (2)
        List<String> availableWorkers = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(workerServiceUrl + "/api/workers/available")
                        .queryParam("count", 2)
                        .queryParam("start", dto.getTimeSlotStart())
                        .queryParam("end", dto.getTimeSlotEnd())
                        .build())
                .retrieve()
                .bodyToFlux(String.class)
                .collectList()
                .block(Duration.ofSeconds(5));
        if (availableWorkers == null
                || !availableWorkers.contains(dto.getWorker1Id())
                || !availableWorkers.contains(dto.getWorker2Id())
                || dto.getWorker1Id().equals(dto.getWorker2Id())) {
            throw new InvalidPickUpRequestException("One or both workers not available or identical.");
        }
        logger.debug("Workers {} and {} validated", dto.getWorker1Id(), dto.getWorker2Id());

        // 6. Generate ID and persist
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
        logger.info("Persisted PickUp with ID: {}", newId);

        // 7. Notify logging module
        webClient.post()
                .uri(loggingServiceUrl + "/api/logs")
                .bodyValue(
                        new LogEntry("CREATE_PICKUP", "Created PickUp ID: " + newId)
                )
                .retrieve()
                .bodyToMono(Void.class)
                .block(Duration.ofSeconds(5));
        logger.debug("Logged creation event for PickUp ID: {}", newId);

        // 8. Occupy vehicle and workers
        webClient.put()
                .uri(vehicleServiceUrl + "/api/vehicles/occupy/{vehicleId}", dto.getVehicleId())
                .retrieve()
                .bodyToMono(Void.class)
                .block(Duration.ofSeconds(5));
        logger.debug("Vehicle {} marked occupied", dto.getVehicleId());

        webClient.put()
                .uri(workerServiceUrl + "/api/workers/occupy/{workerId}", dto.getWorker1Id())
                .retrieve()
                .bodyToMono(Void.class)
                .block(Duration.ofSeconds(5));
        webClient.put()
                .uri(workerServiceUrl + "/api/workers/occupy/{workerId}", dto.getWorker2Id())
                .retrieve()
                .bodyToMono(Void.class)
                .block(Duration.ofSeconds(5));
        logger.debug("Workers {} and {} marked occupied", dto.getWorker1Id(), dto.getWorker2Id());

        return newId;
    }

    @Override
    @Transactional
    public DeletePickUpResponseDto deletePickUp(String pickUpId) {
        logger.info("Received request to delete PickUp ID: {}", pickUpId);

        PickUp existing = repository.findById(pickUpId)
                .orElseThrow(() -> new PickUpNotFoundException(pickUpId));

        // 1. Free vehicle
        webClient.put()
                .uri(vehicleServiceUrl + "/api/vehicles/free/{vehicleId}", existing.getVehicleId())
                .retrieve()
                .bodyToMono(Void.class)
                .block(Duration.ofSeconds(5));
        logger.debug("Vehicle {} freed", existing.getVehicleId());

        // 2. Free workers
        webClient.put()
                .uri(workerServiceUrl + "/api/workers/free/{workerId}", existing.getWorker1Id())
                .retrieve()
                .bodyToMono(Void.class)
                .block(Duration.ofSeconds(5));
        webClient.put()
                .uri(workerServiceUrl + "/api/workers/free/{workerId}", existing.getWorker2Id())
                .retrieve()
                .bodyToMono(Void.class)
                .block(Duration.ofSeconds(5));
        logger.debug("Workers {} and {} freed", existing.getWorker1Id(), existing.getWorker2Id());

        // 3. Delete entity
        repository.delete(existing);
        logger.info("Deleted PickUp ID: {}", pickUpId);

        // 4. Notify logging
        webClient.post()
                .uri(loggingServiceUrl + "/api/logs")
                .bodyValue(new LogEntry("DELETE_PICKUP", "Deleted PickUp ID: " + pickUpId))
                .retrieve()
                .bodyToMono(Void.class)
                .block(Duration.ofSeconds(5));
        logger.debug("Logged deletion event for PickUp ID: {}", pickUpId);

        return new DeletePickUpResponseDto(pickUpId, "DELETED");
    }

    @Override
    public List<PickUpDto> listAllPickUps() {
        logger.info("Listing all PickUps");
        List<PickUp> all = repository.findAll();
        logger.debug("Found {} pickups", all.size());
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
                .collect(Collectors.toList());
    }

    @Override
    public PickUpDto getPickUpById(String pickUpId) {
        logger.info("Fetching PickUp by ID: {}", pickUpId);
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

    /**
     * Inner DTO for logging payload.
     */
    @Data
    @AllArgsConstructor
    private static class LogEntry {
        private String eventType;
        private String details;
    }
}
