package com.company.alloca.controller;

import com.company.alloca.dto.AllocationRequest;
import com.company.alloca.dto.AllocationResponse;
import com.company.alloca.dto.WorkloadDto;
import com.company.alloca.service.AllocationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/allocations")
@RequiredArgsConstructor
@Slf4j
public class AllocationController {

    private final AllocationService service;

    @PostMapping
    public ResponseEntity<AllocationResponse> createAllocation(@Valid @RequestBody AllocationRequest request) {
        log.info("Received request to create allocation for employee: {}", request.getEmployeeId());
        return ResponseEntity.ok(service.createAllocation(request));
    }

    @GetMapping
    public ResponseEntity<List<AllocationResponse>> getAllAllocations() {
        log.info("Received request to get all allocations");
        return ResponseEntity.ok(service.getAllAllocations());
    }

    @PutMapping("/{id}")
    public ResponseEntity<AllocationResponse> updateAllocation(@PathVariable Long id,
                                                               @Valid @RequestBody AllocationRequest request) {
        log.info("Received request to update allocation ID: {}", id);
        return ResponseEntity.ok(service.updateAllocation(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAllocation(@PathVariable Long id) {
        log.info("Received request to delete allocation ID: {}", id);
        service.deleteAllocation(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<AllocationResponse> activateAllocation(@PathVariable Long id) {
        log.info("Received request to activate allocation ID: {}", id);
        return ResponseEntity.ok(service.activateAllocation(id));
    }

    @PutMapping("/{id}/end")
    public ResponseEntity<AllocationResponse> endAllocation(@PathVariable Long id) {
        log.info("Received request to end allocation ID: {}", id);
        return ResponseEntity.ok(service.endAllocation(id));
    }
}