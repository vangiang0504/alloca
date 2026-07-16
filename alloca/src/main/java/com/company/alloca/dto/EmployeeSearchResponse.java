package com.company.alloca.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeSearchResponse {
    private String employeeName;
    private Integer available;

    public EmployeeSearchResponse(String employeeName, Long totalAllocation) {
        this.employeeName = employeeName;
        int total = totalAllocation != null ? totalAllocation.intValue() : 0;
        this.available = 100 - total;
    }
}
