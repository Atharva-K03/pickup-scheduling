package com.wastewise.pickup.controller;

import com.wastewise.pickup.dto.CreatePickUpDto;
import com.wastewise.pickup.dto.DeletePickUpResponseDto;
import com.wastewise.pickup.dto.PickUpDto;
import com.wastewise.pickup.service.PickUpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for PickUp endpoints.
 */
@RestController
@RequestMapping("/pickup")
@RequiredArgsConstructor
public class PickUpController {

    private static final Logger logger = LoggerFactory.getLogger(PickUpController.class);
    private final PickUpService pickUpService;

    /**
     * Create a new PickUp.
     * @param dto CreatePickUpDto payload.
     * @return generated pickUpId.
     */
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public String createPickUp(@Valid @RequestBody CreatePickUpDto dto) {
        logger.info("POST /pickup/create - payload: {}", dto);
        String id = pickUpService.createPickUp(dto);
        logger.debug("Created PickUp with ID: {}", id);
        return id;
    }

    /**
     * Delete an existing PickUp.
     * @param id identifier of the pickup to delete.
     */
    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePickUp(@PathVariable String id) {
        logger.info("DELETE /pickup/delete/{}", id);
        pickUpService.deletePickUp(id);
        logger.debug("Deleted PickUp with ID: {}", id);
    }

    /**
     * List all pickups.
     * @return list of PickUpDto.
     */
    @GetMapping("/all")
    public List<PickUpDto> listAllPickUps() {
        logger.info("GET /pickup/all");
        List<PickUpDto> all = pickUpService.listAllPickUps();
        logger.debug("Returning {} pickups", all.size());
        return all;
    }

    /**
     * Get a single PickUp by ID.
     * @param id identifier to fetch.
     * @return PickUpDto for that ID.
     */
    @GetMapping("/get/{id}")
    public PickUpDto getPickUpById(@PathVariable String id) {
        logger.info("GET /pickup/get/{}", id);
        PickUpDto dto = pickUpService.getPickUpById(id);
        logger.debug("Fetched PickUp: {}", dto);
        return dto;
    }
}
