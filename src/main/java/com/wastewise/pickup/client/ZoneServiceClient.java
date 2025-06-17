package com.wastewise.pickup.client;

import com.wastewise.pickup.dto.ZoneDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "zone-service")
public interface ZoneServiceClient {
    @GetMapping("/zones")
    List<ZoneDto> getAllZones();
}