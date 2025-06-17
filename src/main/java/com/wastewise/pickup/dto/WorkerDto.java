package com.wastewise.pickup.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkerDto {
    private String id;
    private String name;
    private String status; // AVAILABLE, OCCUPIED, OFF_DUTY
    private String skill;
}