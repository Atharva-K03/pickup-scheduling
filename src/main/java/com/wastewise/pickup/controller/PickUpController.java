package com.wastewise.pickup.controller;

import com.wastewise.pickup.dto.CreatePickUpDto;
import com.wastewise.pickup.dto.PickUpDto;
import com.wastewise.pickup.service.PickUpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing PickUp operations.
 * Provides endpoints for creating, deleting, retrieving, and listing PickUp resources.
 */
@RestController
@RequestMapping("/wastewise/scheduler/pickups")
@RequiredArgsConstructor
@Slf4j
public class PickUpController {

    private final PickUpService pickUpService;

    /**
     * Creates a new PickUp resource.
     *
     * @param dto the DTO containing the PickUp creation details
     * @return a ResponseEntity containing the ID of the created PickUp and HTTP status 201 (Created)
     */
    @PostMapping
    public ResponseEntity<String> createPickUp(@Valid @RequestBody CreatePickUpDto dto) {
        log.info("POST /pickups - payload: {}", dto);
        String id = pickUpService.createPickUp(dto);
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.CREATED).body(id);
        log.debug("Created PickUp with ID: {}", id);
        return response;
    }

    /**
     * Deletes an existing PickUp resource by its ID.
     *
     * @param id the ID of the PickUp to delete
     * @return a ResponseEntity with HTTP status 204 (No Content)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePickUp(@PathVariable String id) {
        log.info("DELETE /pickups/{}", id);
        pickUpService.deletePickUp(id);
        log.debug("Deleted PickUp with ID: {}", id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves all existing PickUp resources.
     *
     * @return a ResponseEntity containing a list of all PickUp DTOs and HTTP status 200 (OK)
     */
    @GetMapping
    public ResponseEntity<List<PickUpDto>> listAllPickUps() {
        log.info("GET /pickups");
        List<PickUpDto> all = pickUpService.listAllPickUps();
        log.debug("Returning {} pickups", all.size());
        return ResponseEntity.ok(all);
    }

    /**
     * Retrieves a specific PickUp resource by its ID.
     *
     * @param id the ID of the PickUp to retrieve
     * @return a ResponseEntity containing the PickUp DTO and HTTP status 200 (OK)
     */
    @GetMapping("/{id}")
    public ResponseEntity<PickUpDto> getPickUpById(@PathVariable String id) {
        log.info("GET /pickups/{}", id);
        PickUpDto dto = pickUpService.getPickUpById(id);
        log.debug("Fetched PickUp: {}", dto);
        return ResponseEntity.ok(dto);
    }
}