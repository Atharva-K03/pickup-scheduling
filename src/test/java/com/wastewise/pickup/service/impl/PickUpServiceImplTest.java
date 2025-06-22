package com.wastewise.pickup.service.impl;

import com.wastewise.pickup.dto.CreatePickUpDto;
import com.wastewise.pickup.dto.DeletePickUpResponseDto;
import com.wastewise.pickup.dto.PickUpDto;
import com.wastewise.pickup.exception.InvalidPickUpRequestException;
import com.wastewise.pickup.exception.PickUpNotFoundException;
import com.wastewise.pickup.model.PickUp;
import com.wastewise.pickup.model.enums.Frequency;
import com.wastewise.pickup.model.enums.PickUpStatus;
import com.wastewise.pickup.repository.PickUpRepository;
import com.wastewise.pickup.utility.IdGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class PickUpServiceImplTest {

    @Mock
    private PickUpRepository pickUpRepository;

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private PickUpServiceImpl pickUpService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreatePickUp_Success() {
        // Arrange
        String expectedId = "P123";
        CreatePickUpDto createPickUpDto = new CreatePickUpDto();
        // Set required details in the DTO
        createPickUpDto.setZoneId("Z001");
        createPickUpDto.setTimeSlotStart(LocalDateTime.now());
        createPickUpDto.setTimeSlotEnd(LocalDateTime.now().plusHours(1));
        createPickUpDto.setLocationName("Test Location");

        PickUp pickUp = new PickUp();
        pickUp.setId(expectedId);

        when(idGenerator.generatePickUpId()).thenReturn(expectedId);
        when(pickUpRepository.save(any(PickUp.class))).thenReturn(pickUp);

        // Act
        String resultId = pickUpService.createPickUp(createPickUpDto);

        // Assert
        assertEquals(expectedId, resultId);
        verify(idGenerator, times(1)).generatePickUpId();
        verify(pickUpRepository, times(1)).save(any(PickUp.class));
    }

    @Test
    void testCreatePickUp_InvalidRequest() {
        // Arrange
        CreatePickUpDto createPickUpDto = new CreatePickUpDto(); // Missing required fields (invalid)

        // Act & Assert
        assertThrows(InvalidPickUpRequestException.class, () -> pickUpService.createPickUp(createPickUpDto));
        verify(pickUpRepository, never()).save(any(PickUp.class)); // No interaction expected
    }

    @Test
    void testDeletePickUp_Success() {
        // Arrange
        String pickUpId = "P123";
        PickUp pickUp = new PickUp();
        pickUp.setId(pickUpId);

        when(pickUpRepository.findById(pickUpId)).thenReturn(Optional.of(pickUp));

        // Act
        DeletePickUpResponseDto response = pickUpService.deletePickUp(pickUpId);

        // Assert
        assertEquals(pickUpId, response.getPickUpId());
        assertEquals("DELETED", response.getStatus());
        verify(pickUpRepository, times(1)).delete(pickUp);
    }

    @Test
    void testDeletePickUp_NotFound() {
        // Arrange
        String pickUpId = "P123";
        when(pickUpRepository.findById(pickUpId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(PickUpNotFoundException.class, () -> pickUpService.deletePickUp(pickUpId));
        verify(pickUpRepository, never()).delete(any(PickUp.class));
    }

    @Test
    void testListAllPickUps_Success() {
        // Arrange
        List<PickUp> mockPickUps = List.of(
                new PickUp("P001", "Z001", LocalDateTime.now(), LocalDateTime.now().plusHours(1),
                        Frequency.DAILY, "Location 1", "V001", "W001", "W002", PickUpStatus.SCHEDULED),
                new PickUp("P002", "Z002", LocalDateTime.now(), LocalDateTime.now().plusHours(2),
                        Frequency.WEEKLY, "Location 2", "V002", "W003", "W004", PickUpStatus.COMPLETED)
        );

        when(pickUpRepository.findAll()).thenReturn(mockPickUps);

        // Act
        List<PickUpDto> result = pickUpService.listAllPickUps();

        // Assert
        assertEquals(2, result.size());
        assertThat(result).extracting(PickUpDto::getId).containsExactly("P001", "P002");
        verify(pickUpRepository, times(1)).findAll();
    }

    @Test
    void testGetPickUpById_Success() {
        // Arrange
        String pickUpId = "P123";
        PickUp mockPickUp = new PickUp("P123", "Z001", LocalDateTime.now(),
                LocalDateTime.now().plusHours(2), Frequency.DAILY,
                "Test Location", "V001", "W001", "W002", PickUpStatus.SCHEDULED);

        when(pickUpRepository.findById(pickUpId)).thenReturn(Optional.of(mockPickUp));

        // Act
        PickUpDto result = pickUpService.getPickUpById(pickUpId);

        // Assert
        assertEquals(pickUpId, result.getId());
        assertEquals("Z001", result.getZoneId());
        assertEquals("Test Location", result.getLocationName());
        verify(pickUpRepository, times(1)).findById(pickUpId);
    }

    @Test
    void testGetPickUpById_NotFound() {
        // Arrange
        String pickUpId = "P123";
        when(pickUpRepository.findById(pickUpId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(PickUpNotFoundException.class, () -> pickUpService.getPickUpById(pickUpId));
        verify(pickUpRepository, times(1)).findById(pickUpId);
    }
}