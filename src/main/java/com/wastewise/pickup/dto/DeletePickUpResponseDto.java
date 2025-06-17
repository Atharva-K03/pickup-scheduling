// File: src/main/java/com/wastewise/pickup/dto/DeletePickUpResponseDto.java
package com.wastewise.pickup.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for delete pickup response.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeletePickUpResponseDto {
    private String pickUpId;
    private String status;
}
