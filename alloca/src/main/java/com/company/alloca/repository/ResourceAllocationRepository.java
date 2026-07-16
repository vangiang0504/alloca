package com.company.alloca.repository;

import com.company.alloca.entity.ResourceAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceAllocationRepository extends JpaRepository<ResourceAllocation, Long> {

    List<ResourceAllocation> findByEmployeeEmployeeId(Long employeeId);

    List<ResourceAllocation> findByProjectProjectId(Long projectId);

    @Query("SELECT ra FROM ResourceAllocation ra WHERE ra.employee.employeeId = :employeeId " +
           "AND ra.status <> com.company.alloca.entity.AllocationStatus.ENDED " +
           "AND ra.startDate <= :endDate AND ra.endDate >= :startDate")
    List<ResourceAllocation> findOverlappingAllocations(@Param("employeeId") Long employeeId,
                                                        @Param("startDate") java.time.LocalDate startDate,
                                                        @Param("endDate") java.time.LocalDate endDate);

    @Query("SELECT COALESCE(SUM(ra.allocationPercent), 0) FROM ResourceAllocation ra " +
           "WHERE ra.employee.employeeId = :employeeId AND ra.status <> com.company.alloca.entity.AllocationStatus.ENDED " +
           "AND ra.startDate <= :currentDate AND ra.endDate >= :currentDate")
    Integer sumAllocationByEmployeeIdAndDate(@Param("employeeId") Long employeeId, @Param("currentDate") java.time.LocalDate currentDate);

    @Query("SELECT new com.company.alloca.dto.EmployeeUtilizationDto(ra.employee.employeeId, ra.employee.fullName, COALESCE(SUM(ra.allocationPercent), 0L)) " +
           "FROM ResourceAllocation ra WHERE ra.status <> com.company.alloca.entity.AllocationStatus.ENDED " +
           "AND ra.startDate <= :currentDate AND ra.endDate >= :currentDate " +
           "GROUP BY ra.employee.employeeId, ra.employee.fullName " +
           "ORDER BY SUM(ra.allocationPercent) DESC")
    List<com.company.alloca.dto.EmployeeUtilizationDto> getUtilizationReport(@Param("currentDate") java.time.LocalDate currentDate);

    @Query("SELECT new com.company.alloca.dto.AvailableResourceDto(ra.employee.employeeId, ra.employee.fullName, ra.employee.role, ra.employee.department, (100 - COALESCE(SUM(ra.allocationPercent), 0L))) " +
           "FROM ResourceAllocation ra WHERE ra.status <> com.company.alloca.entity.AllocationStatus.ENDED " +
           "AND ra.startDate <= :currentDate AND ra.endDate >= :currentDate " +
           "GROUP BY ra.employee.employeeId, ra.employee.fullName, ra.employee.role, ra.employee.department " +
           "HAVING SUM(ra.allocationPercent) < 100")
    List<com.company.alloca.dto.AvailableResourceDto> getAvailableResources(@Param("currentDate") java.time.LocalDate currentDate);

    @Query("SELECT new com.company.alloca.dto.EmployeeUtilizationDto(ra.employee.employeeId, ra.employee.fullName, COALESCE(SUM(ra.allocationPercent), 0L)) " +
           "FROM ResourceAllocation ra WHERE ra.status <> com.company.alloca.entity.AllocationStatus.ENDED " +
           "AND ra.startDate <= :currentDate AND ra.endDate >= :currentDate " +
           "GROUP BY ra.employee.employeeId, ra.employee.fullName " +
           "HAVING SUM(ra.allocationPercent) > 90")
    List<com.company.alloca.dto.EmployeeUtilizationDto> getOverloadedEmployees(@Param("currentDate") java.time.LocalDate currentDate);
}