package com.company.alloca.service;

import com.company.alloca.dto.AllocationRequest;
import com.company.alloca.dto.AllocationResponse;
import com.company.alloca.dto.AvailableResourceDto;
import com.company.alloca.dto.EmployeeUtilizationDto;
import com.company.alloca.dto.WorkloadDto;
import com.company.alloca.entity.Employee;
import com.company.alloca.entity.Project;
import com.company.alloca.entity.ProjectStatus;
import com.company.alloca.entity.AllocationStatus;
import com.company.alloca.entity.ResourceAllocation;
import com.company.alloca.repository.EmployeeRepository;
import com.company.alloca.repository.ProjectRepository;
import com.company.alloca.repository.ResourceAllocationRepository;
import com.company.alloca.exception.EmployeeNotFoundException;
import com.company.alloca.exception.ProjectNotFoundException;
import com.company.alloca.exception.AllocationNotFoundException;
import com.company.alloca.exception.AllocationExceededException;
import com.company.alloca.exception.ProjectStatusException;
import com.company.alloca.exception.InvalidAllocationStatusException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AllocationService {

    private final ResourceAllocationRepository allocationRepository;
    private final EmployeeRepository employeeRepository;
    private final ProjectRepository projectRepository;

    public AllocationResponse createAllocation(AllocationRequest request) {
        Employee employee = employeeRepository.findWithLockByEmployeeId(request.getEmployeeId()).orElseThrow(() ->
                new EmployeeNotFoundException("Employee not found with ID " + request.getEmployeeId()));

        validateCapacityOverlap(request.getEmployeeId(), request.getStartDate(), request.getEndDate(), request.getAllocationPercent(), null);
        Project project = getValidProjectForAllocation(request.getProjectId());

        ResourceAllocation allocation = ResourceAllocation.builder()
                .employee(employee)
                .project(project)
                .allocationPercent(request.getAllocationPercent())
                .roleInProject(request.getRoleInProject())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status(AllocationStatus.PENDING)
                .build();

        allocationRepository.save(allocation);
        log.info("Created allocation ID {} for employee {}", allocation.getAllocationId(), employee.getEmployeeId());
        return toResponse(allocation);
    }

    public AllocationResponse updateAllocation(Long id, AllocationRequest request) {
        ResourceAllocation allocation = allocationRepository.findById(id).orElseThrow(() ->
                new AllocationNotFoundException("Allocation not found with ID " + id));

        Employee employee = employeeRepository.findWithLockByEmployeeId(request.getEmployeeId()).orElseThrow(() ->
                new EmployeeNotFoundException("Employee not found with ID " + request.getEmployeeId()));

        validateCapacityOverlap(request.getEmployeeId(), request.getStartDate(), request.getEndDate(), request.getAllocationPercent(), id);
        Project project = getValidProjectForAllocation(request.getProjectId());

        allocation.setEmployee(employee);
        allocation.setProject(project);
        allocation.setAllocationPercent(request.getAllocationPercent());
        allocation.setRoleInProject(request.getRoleInProject());
        allocation.setStartDate(request.getStartDate());
        allocation.setEndDate(request.getEndDate());

        allocationRepository.save(allocation);
        log.info("Updated allocation ID {}", id);
        return toResponse(allocation);
    }

    public void deleteAllocation(Long id) {
        ResourceAllocation allocation = allocationRepository.findById(id).orElseThrow(() ->
                new AllocationNotFoundException("Allocation not found with ID " + id));
        if (allocation.getStatus() == AllocationStatus.ACTIVE) {
            throw new InvalidAllocationStatusException("Cannot delete an ACTIVE allocation");
        }
        allocationRepository.deleteById(id);
        log.info("Deleted allocation ID {}", id);
    }

    public List<AllocationResponse> getAllAllocations() {
        return allocationRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public WorkloadDto getEmployeeWorkload(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId).orElseThrow(() ->
                new EmployeeNotFoundException("Employee not found with ID " + employeeId));
        Integer total = allocationRepository.sumAllocationByEmployeeIdAndDate(employeeId, LocalDate.now());
        if (total == null) total = 0;
        int available = 100 - total;
        return WorkloadDto.builder()
                .employeeId(employeeId)
                .employeeName(employee.getFullName())
                .allocated(total)
                .available(available)
                .build();
    }

    public List<EmployeeUtilizationDto> getUtilizationReport() {
        return allocationRepository.getUtilizationReport(LocalDate.now());
    }

    public List<AvailableResourceDto> getAvailableResources() {
        return allocationRepository.getAvailableResources(LocalDate.now());
    }

    public List<EmployeeUtilizationDto> getOverloadedEmployees() {
        return allocationRepository.getOverloadedEmployees(LocalDate.now());
    }

    public AllocationResponse activateAllocation(Long id) {
        ResourceAllocation allocation = allocationRepository.findById(id).orElseThrow(() ->
                new AllocationNotFoundException("Allocation not found with ID " + id));

        if (allocation.getStatus() != AllocationStatus.PENDING) {
            throw new InvalidAllocationStatusException(
                    "Only PENDING allocations can be activated. Current status: " + allocation.getStatus());
        }

        allocation.setStatus(AllocationStatus.ACTIVE);
        allocationRepository.save(allocation);
        log.info("Activated allocation ID {}", id);
        return toResponse(allocation);
    }

    public AllocationResponse endAllocation(Long id) {
        ResourceAllocation allocation = allocationRepository.findById(id).orElseThrow(() ->
                new AllocationNotFoundException("Allocation not found with ID " + id));

        if (allocation.getStatus() != AllocationStatus.ACTIVE) {
            throw new InvalidAllocationStatusException(
                    "Only ACTIVE allocations can be ended. Current status: " + allocation.getStatus());
        }

        allocation.setStatus(AllocationStatus.ENDED);
        allocationRepository.save(allocation);
        log.info("Ended allocation ID {}", id);
        return toResponse(allocation);
    }

    private AllocationResponse toResponse(ResourceAllocation a) {
        return AllocationResponse.builder()
                .allocationId(a.getAllocationId())
                .employeeId(a.getEmployee().getEmployeeId())
                .employeeName(a.getEmployee().getFullName())
                .projectId(a.getProject().getProjectId())
                .projectName(a.getProject().getProjectName())
                .allocationPercent(a.getAllocationPercent())
                .roleInProject(a.getRoleInProject())
                .startDate(a.getStartDate().toString())
                .endDate(a.getEndDate().toString())
                .status(a.getStatus().name())
                .build();
    }

    private void validateCapacityOverlap(Long employeeId, LocalDate startDate, LocalDate endDate, Integer requestedPercent, Long excludeAllocationId) {
        List<ResourceAllocation> overlaps = allocationRepository.findOverlappingAllocations(employeeId, startDate, endDate);

        int maxDailyUtilization = 0;
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            final LocalDate current = date;
            int dailySum = overlaps.stream()
                    .filter(ra -> excludeAllocationId == null || !ra.getAllocationId().equals(excludeAllocationId))
                    .filter(ra -> !current.isBefore(ra.getStartDate()) && !current.isAfter(ra.getEndDate()))
                    .mapToInt(ResourceAllocation::getAllocationPercent).sum();
            maxDailyUtilization = Math.max(maxDailyUtilization, dailySum);
        }

        if (maxDailyUtilization + requestedPercent > 100) {
            log.warn("Allocation exceeded for employee {} on date range. Max Current: {}, Requested: {}", employeeId, maxDailyUtilization, requestedPercent);
            throw new AllocationExceededException("Employee allocation exceeds 100% on some dates in the requested range");
        }
    }

    private Project getValidProjectForAllocation(Long projectId) {
        Project project = projectRepository.findById(projectId).orElseThrow(() ->
                new ProjectNotFoundException("Project not found with ID " + projectId));

        if (project.getStatus() == ProjectStatus.COMPLETED) {
            throw new ProjectStatusException("Cannot allocate to a COMPLETED project");
        }
        return project;
    }
}