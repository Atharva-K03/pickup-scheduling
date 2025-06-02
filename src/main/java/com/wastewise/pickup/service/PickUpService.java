package com.wastewise.pickup.service;

import com.wastewise.pickup.dto.CreatePickUpDto;
import com.wastewise.pickup.dto.DeletePickUpResponseDto;
import com.wastewise.pickup.dto.PickUpDto;

import java.util.List;

/**
 * Service interface for PickUp operations.
 */
public interface PickUpService {

    /**
     * Create a new PickUp job.
     * @param dto data for creating pickup.
     * @return generated pickUpId.
     */
    String createPickUp(CreatePickUpDto dto);

    /**
     * Delete an existing PickUp by ID.
     * @param pickUpId identifier to delete.
     * @return response DTO with deletion status.
     */
    DeletePickUpResponseDto deletePickUp(String pickUpId);

    /**
     * List all pickups for dashboard.
     * @return list of PickUpDto.
     */
    List<PickUpDto> listAllPickUps();

    /**
     * Get one PickUp by ID.
     * @param pickUpId identifier to fetch.
     * @return PickUpDto for the given ID.
     */
    PickUpDto getPickUpById(String pickUpId);
}
