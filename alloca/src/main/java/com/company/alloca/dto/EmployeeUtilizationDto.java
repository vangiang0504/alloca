package com.company.alloca.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeUtilizationDto {
    private Long employeeId;
    private String employeeName;
    private Integer totalAllocation;

    public EmployeeUtilizationDto(Long employeeId, String employeeName, Long totalAllocation) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.totalAllocation = totalAllocation != null ? totalAllocation.intValue() : 0;
    }
}