//package com.wastewise.pickup.service.impl;
//
//import com.wastewise.pickup.dto.CreatePickUpDto;
//import com.wastewise.pickup.dto.DeletePickUpResponseDto;
//import com.wastewise.pickup.dto.VehicleStatusUpdateDto;
//import com.wastewise.pickup.dto.WorkerStatusUpdateDto;
//import com.wastewise.pickup.exception.InvalidPickUpRequestException;
//import com.wastewise.pickup.exception.PickUpNotFoundException;
//import com.wastewise.pickup.model.PickUp;
//import com.wastewise.pickup.model.enums.PickUpStatus;
//import com.wastewise.pickup.repository.PickUpRepository;
//import com.wastewise.pickup.service.PickUpService;
//import com.wastewise.pickup.utility.IdGenerator;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.reactive.function.client.WebClient;
//
//import java.time.Duration;
///**
// * This service handles the creation and deletion of PickUp jobs, including validations
// * and interactions with external services for zones, vehicles, and workers.
// */
//@Service
//@Slf4j
//public class PickUpServiceImpl implements PickUpService {
//
//    private final PickUpRepository repository;
//    private final IdGenerator idGenerator;
//    private final WebClient webClient;
//    private final String zoneServiceUrl;
//    private final String vehicleServiceUrl;
//    private final String workerServiceUrl;
//    private static final String STATUS_OCCUPIED = "OCCUPIED";
//    private static final String STATUS_AVAILABLE = "AVAILABLE";
//
//
//    /**
//     * Constructor for PickUpServiceImpl.
//     */
//    public PickUpServiceImpl(
//            PickUpRepository repository,
//            IdGenerator idGenerator,
//            WebClient.Builder webClientBuilder,
//            @Value("${zone-service.url}") String zoneServiceUrl,
//            @Value("${vehicle-service.url}") String vehicleServiceUrl,
//            @Value("${worker-service.url}") String workerServiceUrl) {
//        this.repository = repository;
//        this.idGenerator = idGenerator;
//        this.webClient = webClientBuilder.build();
//        this.zoneServiceUrl = zoneServiceUrl;
//        this.vehicleServiceUrl = vehicleServiceUrl;
//        this.workerServiceUrl = workerServiceUrl;
//    }
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
//                .filter(Boolean.TRUE::equals)
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
//     * Notifies the Worker Service.
//     */
//    private void notifyWorkerService(String workerId, String status) {
//        try {
//            log.info("Notifying worker {} with status: {}", workerId, status);
//
//            webClient.post()
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
//            webClient.post()
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
///**
// * Improvements and Notes:
// *
// * The WebClient is used to make synchronous calls to these services. Asynchronous handling can be implemented if needed.
// *
// * Wrap Asynchronous Calls With Retry Logic (spring-retry) for better resilience.
// *
// * Error handling can be provided for notifications to external services.
// *
// * The code is designed to be used in a microservices architecture where each service is responsible for its own domain logic.
// *
// */
