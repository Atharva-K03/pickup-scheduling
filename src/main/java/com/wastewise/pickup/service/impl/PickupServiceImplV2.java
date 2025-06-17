//package com.wastewise.pickup.service.impl;
//
//import com.wastewise.pickup.dto.*;
//import com.wastewise.pickup.exception.InvalidPickUpRequestException;
//import com.wastewise.pickup.exception.PickUpNotFoundException;
//import com.wastewise.pickup.model.PickUp;
//import com.wastewise.pickup.model.enums.PickUpStatus;
//import com.wastewise.pickup.repository.PickUpRepository;
//import com.wastewise.pickup.service.PickUpService;
//import com.wastewise.pickup.utility.IdGenerator;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.reactive.function.client.WebClient;
//
//import java.time.Duration;
//import java.util.List;
//
///**
// * This service handles the creation and deletion of PickUp jobs, including validations
// * and interactions with external services for zones, vehicles, and workers.
// */
//@Service
//@Slf4j
//@RequiredArgsConstructor
//public class PickUpServiceImpl implements PickUpService {
//
//    private final PickUpRepository repository;
//    private final IdGenerator idGenerator;
//    private final WebClient webClient;
//    private final String zoneServiceUrl;
//    private final String vehicleServiceUrl;
//    private final String workerServiceUrl;
//    /**
//     * Status constants for notifying Worker and Vehicle Services.
//     */
//    private static final String STATUS_OCCUPIED = "OCCUPIED";
//    private static final String STATUS_AVAILABLE = "AVAILABLE";
//
//
//
//    /**
//     * Creates a new PickUp.
//     */
//    @Override
//    @Transactional
//    public String createPickUp(CreatePickUpDto dto) {
//        log.info("Creating pickup: {}", dto);
//
//        // Validations for the PickUp creation request.
//
//        // 1) Validate time slot
//        if (dto.getTimeSlotEnd().isBefore(dto.getTimeSlotStart().plusMinutes(30))) {
//            throw new InvalidPickUpRequestException("Invalid time slot: end time must be at least 30 minutes after start time.");
//        }
//
//        // 2) Validate zone - check if the zone exists
//        Boolean zoneExists = webClient.get()
//                .uri(zoneServiceUrl + "/{zoneId}", dto.getZoneId())
//                .retrieve()
//                .bodyToMono(Boolean.class)
//                .block(Duration.ofSeconds(1));
//        if (zoneExists == null || !zoneExists) {
//            throw new InvalidPickUpRequestException("Zone not found");
//        }
//
//        // 3) Validate vehicle - check if the vehicle exists and is available
//        Boolean vehicleExists = webClient.get()
//                .uri(vehicleServiceUrl + "/{vehicleId}" + dto.getVehicleId())
//                .retrieve()
//                .bodyToMono(Boolean.class)
//                .block(Duration.ofSeconds(1));
//        if (vehicleExists == null || !vehicleExists) {
//            throw new InvalidPickUpRequestException("Vehicles are not available");
//        }
//
//        // 4) Validate workers - check if at least two valid workers exist
//        long validWorkerCount = dto.getWorkerIds().stream()
//                .map(workerId -> webClient.get()
//                        .uri(workerServiceUrl + "/" + workerId)
//                        .retrieve()
//                        .bodyToMono(Boolean.class)
//                        .block(Duration.ofSeconds(1)))
//                .filter(Boolean.TRUE::equals) // consider only valid workers
//                .count();
//        if (validWorkerCount < 2) {
//            throw new InvalidPickUpRequestException("At least two valid workers must exist.");
//        }
//
//        // Generate ID and save PickUp
//        String pickupId = idGenerator.generatePickUpId();
//        PickUp pickup = PickUp.builder()
//                .id(pickupId)
//                .zoneId(dto.getZoneId())
//                .timeSlotStart(dto.getTimeSlotStart())
//                .timeSlotEnd(dto.getTimeSlotEnd())
//                .frequency(dto.getFrequency())
//                .locationName(dto.getLocationName())
//                .vehicleId(dto.getVehicleId())
//                .worker1Id(dto.getWorker1Id())
//                .worker2Id(dto.getWorker2Id())
//                .status(PickUpStatus.SCHEDULED)
//                .build();
//        repository.save(pickup);
//
//        log.info("Pickup created with ID: {}", pickupId);
//
//        // Notify Worker and Vehicle Services
//        notifyWorkerService(dto.getWorker1Id(), STATUS_OCCUPIED);
//        log.info("Worker 1 {} marked as OCCUPIED", dto.getWorker1Id());
//
//        notifyWorkerService(dto.getWorker2Id(), STATUS_OCCUPIED);
//        log.info("Worker 2 {} marked as OCCUPIED", dto.getWorker2Id());
//
//        notifyVehicleService(dto.getVehicleId(), STATUS_OCCUPIED);
//        log.info("Vehicle {} marked as OCCUPIED", dto.getVehicleId());
//
//        return pickupId;
//    }
//
//    /**
//     * Deletes an existing PickUp.
//     */
//    @Override
//    @Transactional
//    public DeletePickUpResponseDto deletePickUp(String pickUpId) {
//        log.info("Deleting PickUp with ID: {}", pickUpId);
//
//        PickUp pickup = repository.findById(pickUpId)
//                .orElseThrow(() -> new PickUpNotFoundException("Pickup not found with ID: " + pickUpId));
//        repository.delete(pickup);
//
//        log.info("Pickup deleted with ID: {}", pickUpId);
//
//        // Notify Worker and Vehicle Services to set assets "AVAILABLE"
//        notifyWorkerService(pickup.getWorker1Id(), STATUS_AVAILABLE);
//        log.info("Worker 1 {} marked as AVAILABLE", pickup.getWorker1Id());
//
//        notifyWorkerService(pickup.getWorker2Id(), STATUS_AVAILABLE);
//        log.info("Worker 2 {} marked as AVAILABLE", pickup.getWorker2Id());
//
//        notifyVehicleService(pickup.getVehicleId(), STATUS_AVAILABLE);
//        log.info("Vehicle {} marked as AVAILABLE", pickup.getVehicleId());
//
//        return new DeletePickUpResponseDto(pickUpId, "DELETED");
//    }
//
//    /**
//     * Lists all scheduled PickUps.
//     */
//    @Override
//    public List<PickUpDto> listAllPickUps() {
//        log.info("Listing all PickUps");
//        List<PickUp> all = repository.findAll();
//        log.debug("Found {} pickups", all.size());
//        return all.stream()
//                .map(p -> PickUpDto.builder()
//                        .id(p.getId())
//                        .zoneId(p.getZoneId())
//                        .timeSlotStart(p.getTimeSlotStart())
//                        .timeSlotEnd(p.getTimeSlotEnd())
//                        .frequency(p.getFrequency())
//                        .locationName(p.getLocationName())
//                        .vehicleId(p.getVehicleId())
//                        .worker1Id(p.getWorker1Id())
//                        .worker2Id(p.getWorker2Id())
//                        .status(p.getStatus())
//                        .build())
//                .toList();
//    }
//
//    /**
//     * Fetches a PickUp by its ID.
//     */
//    @Override
//    public PickUpDto getPickUpById(String pickUpId) {
//        log.info("Fetching PickUp by ID: {}", pickUpId);
//        PickUp p = repository.findById(pickUpId)
//                .orElseThrow(() -> new PickUpNotFoundException(pickUpId));
//        return PickUpDto.builder()
//                .id(p.getId())
//                .zoneId(p.getZoneId())
//                .timeSlotStart(p.getTimeSlotStart())
//                .timeSlotEnd(p.getTimeSlotEnd())
//                .frequency(p.getFrequency())
//                .locationName(p.getLocationName())
//                .vehicleId(p.getVehicleId())
//                .worker1Id(p.getWorker1Id())
//                .worker2Id(p.getWorker2Id())
//                .status(p.getStatus())
//                .build();
//    }
//    /**
//     * Notifies the Worker Service.
//     */
//    private void notifyWorkerService(String workerId, String status) {
//        try {
//            log.info("Notifying worker {} with status: {}", workerId, status);
//
//            webClient.put()
//                    .uri(workerServiceUrl + "/status")
//                    .bodyValue(new WorkerStatusUpdateDto(workerId, status))
//                    .retrieve()
//                    .toBodilessEntity()
//                    .block(); // synchronous call for now
//
//            log.info("Worker {} successfully notified with status: {}", workerId, status);
//
//        } catch (Exception ex) {
//            log.error("Error notifying worker {}: {}", workerId, ex.getMessage(), ex);
//            throw new InvalidPickUpRequestException(
//                    String.format("Failed to notify worker '%s' with status '%s'", workerId, status));
//        }
//    }
//
//    /**
//     * Notifies the Vehicle Service.
//     */
//    private void notifyVehicleService(String vehicleId, String status) {
//        try {
//            log.info("Notifying vehicle {} with status: {}", vehicleId, status);
//
//            webClient.put()
//                    .uri(vehicleServiceUrl + "/status")
//                    .bodyValue(new VehicleStatusUpdateDto(vehicleId, status))
//                    .retrieve()
//                    .toBodilessEntity()
//                    .block(); // synchronous call for now
//
//            log.info("Vehicle {} successfully notified with status: {}", vehicleId, status);
//
//        } catch (Exception ex) {
//            log.error("Error notifying vehicle {}: {}", vehicleId, ex.getMessage(), ex);
//            throw new InvalidPickUpRequestException(
//                    String.format("Failed to notify vehicle '%s' with status '%s'", vehicleId, status));
//        }
//    }
//}
//
//

//package com.wastewise.pickup.service.impl;
//
//import com.wastewise.pickup.dto.CreatePickUpDto;
//import com.wastewise.pickup.dto.DeletePickUpResponseDto;
//import com.wastewise.pickup.dto.PickUpDto;
//import com.wastewise.pickup.model.enums.PickUpStatus;
//import com.wastewise.pickup.exception.InvalidPickUpRequestException;
//import com.wastewise.pickup.exception.PickUpNotFoundException;
//import com.wastewise.pickup.model.PickUp;
//import com.wastewise.pickup.repository.PickUpRepository;
//import com.wastewise.pickup.service.PickUpService;
//import com.wastewise.pickup.utility.IdGenerator;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class PickUpServiceImpl implements PickUpService {
//
//    private final PickUpRepository repository;
//    private final IdGenerator idGenerator;
//
//    // Enable mock mode for local testing
//    private static final boolean MOCKMODE = true;
//
//    @Override
//    @Transactional
//    public String createPickUp(CreatePickUpDto dto) {
//        log.info("Received request to create PickUp: {}", dto);
//
//        if (dto.getTimeSlotEnd().isBefore(dto.getTimeSlotStart().plusMinutes(30))) {
//            throw new InvalidPickUpRequestException("End time must be at least 30 minutes after start time.");
//        }
//
//        // --- Mock zone check ---
//        if (MOCKMODE) {
//            log.debug("MOCK: Zone {} validated", dto.getZoneId());
//        } else {
//            throw new UnsupportedOperationException("Zone validation requires remote service.");
//        }
//
//        // --- Mock available vehicle ---
//        if (MOCKMODE) {
//            log.debug("MOCK: Vehicle {} validated", dto.getVehicleId());
//        } else {
//            throw new UnsupportedOperationException("Vehicle validation requires remote service.");
//        }
//
//        // --- Mock available workers ---
//        if (MOCKMODE) {
//            if (dto.getWorker1Id().equals(dto.getWorker2Id())) {
//                throw new InvalidPickUpRequestException("Worker 1 and Worker 2 cannot be the same.");
//            }
//            log.debug("MOCK: Workers {} and {} validated", dto.getWorker1Id(), dto.getWorker2Id());
//        } else {
//            throw new UnsupportedOperationException("Worker validation requires remote service.");
//        }
//
//        // --- Save pickup ---
//        String newId = idGenerator.generatePickUpId();
//        PickUp pickUp = PickUp.builder()
//                .id(newId)
//                .zoneId(dto.getZoneId())
//                .timeSlotStart(dto.getTimeSlotStart())
//                .timeSlotEnd(dto.getTimeSlotEnd())
//                .frequency(dto.getFrequency())
//                .locationName(dto.getLocationName())
//                .vehicleId(dto.getVehicleId())
//                .worker1Id(dto.getWorker1Id())
//                .worker2Id(dto.getWorker2Id())
//                .status(PickUpStatus.SCHEDULED)
//                .build();
//        repository.save(pickUp);
//        log.info("Persisted PickUp with ID: {}", newId);
//
//        // --- Mock logging & occupancy ---
//        if (MOCKMODE) {
//            log.debug("MOCK: Logged creation event and marked resources occupied.");
//        }
//
//        return newId;
//    }
//
//    @Override
//    @Transactional
//    public DeletePickUpResponseDto deletePickUp(String pickUpId) {
//        log.info("Received request to delete PickUp ID: {}", pickUpId);
//
//        PickUp existing = repository.findById(pickUpId)
//                .orElseThrow(() -> new PickUpNotFoundException(pickUpId));
//
//        repository.delete(existing);
//        log.info("Deleted PickUp ID: {}", pickUpId);
//
//        if (MOCKMODE) {
//            log.debug("MOCK: Freed vehicle and workers, and logged deletion.");
//        }
//
//        return new DeletePickUpResponseDto(pickUpId, "DELETED");
//    }
//
//    @Override
//    public List<PickUpDto> listAllPickUps() {
//        log.info("Listing all PickUps");
//        List<PickUp> all = repository.findAll();
//        log.debug("Found {} pickups", all.size());
//        return all.stream()
//                .map(p -> PickUpDto.builder()
//                        .id(p.getId())
//                        .zoneId(p.getZoneId())
//                        .timeSlotStart(p.getTimeSlotStart())
//                        .timeSlotEnd(p.getTimeSlotEnd())
//                        .frequency(p.getFrequency())
//                        .locationName(p.getLocationName())
//                        .vehicleId(p.getVehicleId())
//                        .worker1Id(p.getWorker1Id())
//                        .worker2Id(p.getWorker2Id())
//                        .status(p.getStatus())
//                        .build())
//                        .toList();
//    }
//
//    @Override
//    public PickUpDto getPickUpById(String pickUpId) {
//        log.info("Fetching PickUp by ID: {}", pickUpId);
//        PickUp p = repository.findById(pickUpId)
//                .orElseThrow(() -> new PickUpNotFoundException(pickUpId));
//        return PickUpDto.builder()
//                .id(p.getId())
//                .zoneId(p.getZoneId())
//                .timeSlotStart(p.getTimeSlotStart())
//                .timeSlotEnd(p.getTimeSlotEnd())
//                .frequency(p.getFrequency())
//                .locationName(p.getLocationName())
//                .vehicleId(p.getVehicleId())
//                .worker1Id(p.getWorker1Id())
//                .worker2Id(p.getWorker2Id())
//                .status(p.getStatus())
//                .build();
//    }
//
//}