package com.wastewise.pickup.controller;

import com.wastewise.pickup.dto.CreatePickUpDto;
import com.wastewise.pickup.dto.PickUpDto;
import com.wastewise.pickup.model.enums.Frequency;
import com.wastewise.pickup.model.enums.PickUpStatus;
import com.wastewise.pickup.service.PickUpService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class PickUpControllerTest {

    @Mock
    private PickUpService pickUpService;

    @InjectMocks
    private PickUpController pickUpController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreatePickUp() {
        // Arrange
        CreatePickUpDto createPickUpDto = new CreatePickUpDto();
        String expectedId = "P123"; // New format applied
        when(pickUpService.createPickUp(createPickUpDto)).thenReturn(expectedId);

        // Act
        ResponseEntity<String> response = pickUpController.createPickUp(createPickUpDto);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expectedId, response.getBody());
        verify(pickUpService, times(1)).createPickUp(createPickUpDto);
    }

    @Test
    void testDeletePickUp() {
        // Arrange
        String pickUpId = "P100"; // New format applied

        // Act
        ResponseEntity<String> response = pickUpController.deletePickUp(pickUpId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).isEqualTo("Pickup with ID P100 has been deleted successfully.");
        verify(pickUpService, times(1)).deletePickUp(pickUpId);
    }

    @Test
    void testListAllPickUps() {
        // Arrange
        List<PickUpDto> mockPickUpList = List.of(
                PickUpDto.builder()
                        .id("P001") // Pickup ID in PXXX format
                        .zoneId("Z001") // Zone ID in ZXXX format
                        .timeSlotStart(LocalDateTime.now())
                        .timeSlotEnd(LocalDateTime.now().plusHours(2))
                        .frequency(Frequency.DAILY)
                        .locationName("Location 1")
                        .vehicleId("V001") // Vehicle ID in VXXX format
                        .worker1Id("W001") // Worker ID in WXXX format
                        .worker2Id("W002")
                        .status(PickUpStatus.SCHEDULED)
                        .build(),
                PickUpDto.builder()
                        .id("P002")
                        .zoneId("Z002")
                        .timeSlotStart(LocalDateTime.now())
                        .timeSlotEnd(LocalDateTime.now().plusHours(3))
                        .frequency(Frequency.WEEKLY)
                        .locationName("Location 2")
                        .vehicleId("V002")
                        .worker1Id("W003")
                        .worker2Id("W004")
                        .status(PickUpStatus.COMPLETED)
                        .build()
        );
        when(pickUpService.listAllPickUps()).thenReturn(mockPickUpList);

        // Act
        ResponseEntity<List<PickUpDto>> response = pickUpController.listAllPickUps();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        assertThat(response.getBody()).isEqualTo(mockPickUpList);
        verify(pickUpService, times(1)).listAllPickUps();
    }

    @Test
    void testGetPickUpById() {
        // Arrange
        String pickUpId = "P123"; // Pickup ID in PXXX format
        PickUpDto mockDto = PickUpDto.builder()
                .id(pickUpId)
                .zoneId("Z050") // Zone ID in ZXXX format
                .timeSlotStart(LocalDateTime.now())
                .timeSlotEnd(LocalDateTime.now().plusHours(1))
                .frequency(Frequency.MONTHLY)
                .locationName("Mock Location")
                .vehicleId("V123") // Vehicle ID in VXXX format
                .worker1Id("W120") // Worker ID in WXXX format
                .worker2Id("W121")
                .status(PickUpStatus.SCHEDULED)
                .build();
        when(pickUpService.getPickUpById(pickUpId)).thenReturn(mockDto);

        // Act
        ResponseEntity<PickUpDto> response = pickUpController.getPickUpById(pickUpId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).isEqualTo(mockDto);
        verify(pickUpService, times(1)).getPickUpById(pickUpId);
    }
}