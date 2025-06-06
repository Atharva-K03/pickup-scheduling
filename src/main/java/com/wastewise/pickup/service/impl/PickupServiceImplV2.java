//package com.wastewise.pickup.service.impl;
//
//import com.wastewise.pickup.dto.CreatePickUpDto;
//import com.wastewise.pickup.dto.DeletePickUpResponseDto;
//import com.wastewise.pickup.dto.PickUpDto;
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
//
///**
// * Implementation of PickUpService.
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
//        // Validate time slot
//        if (dto.getTimeSlotEnd().isBefore(dto.getTimeSlotStart().plusMinutes(30))) {
//            throw new InvalidPickUpRequestException("Invalid time slot: end time must be at least 30 minutes after start time.");
//        }
//
//        // Validate zone
//        Boolean zoneExists = webClient.get()
//                .uri(zoneServiceUrl + "/api/zones/{zoneId}", dto.getZoneId())
//                .retrieve()
//                .bodyToMono(Boolean.class)
//                .block(Duration.ofSeconds(5));
//        if (zoneExists == null || !zoneExists) {
//            throw new InvalidPickUpRequestException("Zone not found: " + dto.getZoneId());
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
//                .status(PickUpStatus.IN_PROGRESS)
//                .build();
//        repository.save(pickup);
//
//        log.info("Pickup created with ID: {}", pickupId);
//
//        // Notify Worker and Vehicle Services
//        notifyWorkerService(dto.getWorker1Id(), "OCCUPIED");
//        notifyWorkerService(dto.getWorker2Id(), "OCCUPIED");
//        notifyVehicleService(dto.getVehicleId(), "OCCUPIED");
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
//        notifyWorkerService(pickup.getWorker1Id(), "AVAILABLE");
//        notifyWorkerService(pickup.getWorker2Id(), "AVAILABLE");
//        notifyVehicleService(pickup.getVehicleId(), "AVAILABLE");
//
//        return new DeletePickUpResponseDto(pickUpId, "DELETED");
//    }
//
//    /**
//     * Notifies the Worker Service.
//     */
//    private void notifyWorkerService(String workerId, String status) {
//        log.info("Notifying Worker Service: workerId={}, status={}", workerId, status);
//        webClient.put()
//                .uri(workerServiceUrl + "/{workerId}", workerId)
//                .bodyValue(new WorkerStatusUpdateDto(status))
//                .retrieve()
//                .bodyToMono(String.class) // Assuming successful response returns a String
//                .block(Duration.ofSeconds(5));
//        log.info("Worker Service notified for workerId={} with status={}", workerId, status);
//    }
//
//    /**
//     * Notifies the Vehicle Service.
//     */
//    private void notifyVehicleService(String vehicleId, String status) {
//        log.info("Notifying Vehicle Service: vehicleId={}, status={}", vehicleId, status);
//        webClient.put()
//                .uri(vehicleServiceUrl + "/{id}", vehicleId)
//                .bodyValue(new VehicleStatusUpdateDto(vehicleId, status))
//                .retrieve()
//                .bodyToMono(String.class) // Assuming successful response returns a String
//                .block(Duration.ofSeconds(5));
//        log.info("Vehicle Service notified for vehicleId={} with status={}", vehicleId, status);
//    }
//}