package com.company.alloca.service;

import com.company.alloca.dto.EmployeeDto;
import com.company.alloca.dto.EmployeeSearchResponse;
import com.company.alloca.dto.SkillDto;
import com.company.alloca.entity.Employee;
import com.company.alloca.entity.Skill;
import com.company.alloca.repository.EmployeeRepository;
import com.company.alloca.repository.ResourceAllocationRepository;
import com.company.alloca.repository.SkillRepository;
import com.company.alloca.exception.EmployeeHasAllocationsException;
import com.company.alloca.exception.EmployeeNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class EmployeeService {

    private final EmployeeRepository repository;
    private final ResourceAllocationRepository allocationRepository;
    private final SkillRepository skillRepository;

    public EmployeeDto createEmployee(EmployeeDto dto) {
        String code = dto.getEmployeeCode();
        if (code == null || code.isBlank()) {
            code = repository.findMaxEmployeeCode()
                    .map(maxCode -> {
                        int num = Integer.parseInt(maxCode.replace("EMP", "")) + 1;
                        return String.format("EMP%03d", num);
                    })
                    .orElse("EMP001");
        }
        Employee employee = Employee.builder()
                .employeeCode(code)
                .fullName(dto.getFullName())
                .email(dto.getEmail())
                .role(dto.getRole())
                .department(dto.getDepartment())
                .build();
        repository.save(employee);
        
        log.info("Created new employee with ID: {} and Code: {}", employee.getEmployeeId(), code);
        
        return EmployeeDto.builder()
                .employeeId(employee.getEmployeeId())
                .employeeCode(employee.getEmployeeCode())
                .fullName(employee.getFullName())
                .email(employee.getEmail())
                .role(employee.getRole())
                .department(employee.getDepartment())
                .build();
    }

    public EmployeeDto getEmployeeById(Long id) {
        Employee employee = getEmployeeEntity(id);
        return EmployeeDto.builder()
                .employeeId(employee.getEmployeeId())
                .employeeCode(employee.getEmployeeCode())
                .fullName(employee.getFullName())
                .email(employee.getEmail())
                .role(employee.getRole())
                .department(employee.getDepartment())
                .build();
    }

    public List<EmployeeDto> getAllEmployees() {
        return repository.findAll().stream().map(emp -> EmployeeDto.builder()
                .employeeId(emp.getEmployeeId())
                .employeeCode(emp.getEmployeeCode())
                .fullName(emp.getFullName())
                .email(emp.getEmail())
                .role(emp.getRole())
                .department(emp.getDepartment())
                .build()).collect(Collectors.toList());
    }

    public EmployeeDto updateEmployee(Long id, EmployeeDto dto) {
        Employee employee = getEmployeeEntity(id);
        employee.setFullName(dto.getFullName());
        employee.setEmail(dto.getEmail());
        employee.setRole(dto.getRole());
        employee.setDepartment(dto.getDepartment());
        repository.save(employee);
        
        log.info("Updated employee with ID: {}", id);
        
        return EmployeeDto.builder()
                .employeeId(employee.getEmployeeId())
                .employeeCode(employee.getEmployeeCode())
                .fullName(employee.getFullName())
                .email(employee.getEmail())
                .role(employee.getRole())
                .department(employee.getDepartment())
                .build();
    }

    public void deleteEmployee(Long id) {
        if (!repository.existsById(id)) {
            throw new EmployeeNotFoundException("Employee not found with ID " + id);
        }
        if (!allocationRepository.findByEmployeeEmployeeId(id).isEmpty()) {
            throw new EmployeeHasAllocationsException("Cannot delete employee with existing allocations");
        }
        repository.deleteById(id);
        log.info("Deleted employee with ID: {}", id);
    }

    public List<SkillDto> addSkillsToEmployee(Long employeeId, List<String> skillNames) {
        Employee employee = getEmployeeEntity(employeeId);

        if (skillNames == null || skillNames.isEmpty()) {
            return getEmployeeSkills(employeeId);
        }

        List<String> trimmedNames = skillNames.stream()
                .filter(name -> name != null && !name.trim().isEmpty())
                .map(String::trim)
                .distinct()
                .collect(Collectors.toList());

        List<Skill> existingSkills = skillRepository.findByNameInIgnoreCase(trimmedNames);
        
        List<String> existingNames = existingSkills.stream()
                .map(s -> s.getName().toLowerCase())
                .collect(Collectors.toList());

        List<Skill> newSkills = trimmedNames.stream()
                .filter(name -> !existingNames.contains(name.toLowerCase()))
                .map(name -> Skill.builder().name(name).build())
                .collect(Collectors.toList());

        if (!newSkills.isEmpty()) {
            newSkills = skillRepository.saveAll(newSkills);
            existingSkills.addAll(newSkills);
        }

        employee.getSkills().addAll(existingSkills);
        repository.save(employee);
        
        log.info("Added {} skills to employee ID: {}", trimmedNames.size(), employeeId);

        return employee.getSkills().stream()
                .map(s -> SkillDto.builder().skillId(s.getSkillId()).name(s.getName()).build())
                .collect(Collectors.toList());
    }

    public List<SkillDto> getEmployeeSkills(Long employeeId) {
        Employee employee = getEmployeeEntity(employeeId);
        return employee.getSkills().stream()
                .map(s -> SkillDto.builder().skillId(s.getSkillId()).name(s.getName()).build())
                .collect(Collectors.toList());
    }

    public List<EmployeeSearchResponse> searchEmployeesBySkill(String skillName) {
        log.info("Searching employees by skill: {}", skillName);
        return repository.searchEmployeesBySkillWithCapacity(skillName, java.time.LocalDate.now());
    }

    private Employee getEmployeeEntity(Long id) {
        return repository.findById(id).orElseThrow(() ->
                new EmployeeNotFoundException("Employee not found with ID " + id));
    }
}