package com.company.alloca.controller;

import com.company.alloca.dto.EmployeeDto;
import com.company.alloca.dto.EmployeeSearchResponse;
import com.company.alloca.dto.SkillDto;
import com.company.alloca.dto.WorkloadDto;
import com.company.alloca.service.AllocationService;
import com.company.alloca.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
@Slf4j
public class EmployeeController {

    private final EmployeeService service;
    private final AllocationService allocationService;

    @PostMapping
    public ResponseEntity<EmployeeDto> createEmployee(@Valid @RequestBody EmployeeDto dto) {
        log.info("Received request to create employee: {}", dto.getEmail());
        return ResponseEntity.ok(service.createEmployee(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDto> getEmployeeById(@PathVariable Long id) {
        log.info("Received request to get employee ID: {}", id);
        return ResponseEntity.ok(service.getEmployeeById(id));
    }

    @GetMapping
    public ResponseEntity<List<EmployeeDto>> getAllEmployees() {
        log.info("Received request to get all employees");
        return ResponseEntity.ok(service.getAllEmployees());
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeDto> updateEmployee(@PathVariable Long id, @Valid @RequestBody EmployeeDto dto) {
        log.info("Received request to update employee ID: {}", id);
        return ResponseEntity.ok(service.updateEmployee(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        log.info("Received request to delete employee ID: {}", id);
        service.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/skills")
    public ResponseEntity<List<SkillDto>> addSkillsToEmployee(
            @PathVariable Long id, @RequestBody List<String> skillNames) {
        log.info("Received request to add {} skills to employee ID: {}", skillNames.size(), id);
        return ResponseEntity.ok(service.addSkillsToEmployee(id, skillNames));
    }

    @GetMapping("/{id}/skills")
    public ResponseEntity<List<SkillDto>> getEmployeeSkills(@PathVariable Long id) {
        log.info("Received request to get skills for employee ID: {}", id);
        return ResponseEntity.ok(service.getEmployeeSkills(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<EmployeeSearchResponse>> searchBySkill(
            @RequestParam String skill) {
        log.info("Received request to search employees by skill: {}", skill);
        return ResponseEntity.ok(service.searchEmployeesBySkill(skill));
    }

    @GetMapping("/{id}/workload")
    public ResponseEntity<WorkloadDto> getEmployeeWorkload(@PathVariable Long id) {
        log.info("Received request to get workload for employee ID: {}", id);
        return ResponseEntity.ok(allocationService.getEmployeeWorkload(id));
    }
}