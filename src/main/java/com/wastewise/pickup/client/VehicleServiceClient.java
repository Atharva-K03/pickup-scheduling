package com.wastewise.pickup.client;

import com.wastewise.pickup.dto.VehicleDto;
import com.wastewise.pickup.dto.VehicleStatusUpdateDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "vehicle-service")
public interface VehicleServiceClient {
    @GetMapping("/vehicles")
    List<VehicleDto> getAllVehicles();

    @GetMapping("/vehicles/{id}")
    VehicleDto getVehicleById(@PathVariable("id") String id);

    @PutMapping("/vehicles/status")
    void updateVehicleStatus(@RequestBody VehicleStatusUpdateDto updateDto);
}