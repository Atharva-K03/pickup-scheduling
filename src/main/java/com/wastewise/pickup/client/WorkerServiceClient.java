package com.wastewise.pickup.client;

import com.wastewise.pickup.dto.WorkerDto;
import com.wastewise.pickup.dto.WorkerStatusUpdateDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "worker-service")
public interface WorkerServiceClient {
    @GetMapping("/workers")
    List<WorkerDto> getAllWorkers();

    @GetMapping("/workers/{id}")
    WorkerDto getWorkerById(@PathVariable("id") String id);

    @PutMapping("/workers/status")
    void updateWorkerStatus(@RequestBody WorkerStatusUpdateDto updateDto);
}