package com.wastewise.pickup.controller;

import com.wastewise.pickup.dto.CreatePickUpDto;
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
@RequestMapping("/pickups")
@RequiredArgsConstructor
public class PickUpController {

    private static final Logger logger = LoggerFactory.getLogger(PickUpController.class);
    private final PickUpService pickUpService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String createPickUp(@Valid @RequestBody CreatePickUpDto dto) {
        logger.info("POST /pickups - payload: {}", dto);
        String id = pickUpService.createPickUp(dto);
        logger.debug("Created PickUp with ID: {}", id);
        return id;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePickUp(@PathVariable String id) {
        logger.info("DELETE /pickups/{}", id);
        pickUpService.deletePickUp(id);
        logger.debug("Deleted PickUp with ID: {}", id);
    }

    @GetMapping
    public List<PickUpDto> listAllPickUps() {
        logger.info("GET /pickups");
        List<PickUpDto> all = pickUpService.listAllPickUps();
        logger.debug("Returning {} pickups", all.size());
        return all;
    }

    @GetMapping("/{id}")
    public PickUpDto getPickUpById(@PathVariable String id) {
        logger.info("GET /pickups/{}", id);
        PickUpDto dto = pickUpService.getPickUpById(id);
        logger.debug("Fetched PickUp: {}", dto);
        return dto;
    }
}
