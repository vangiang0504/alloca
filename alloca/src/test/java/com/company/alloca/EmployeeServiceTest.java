package com.company.alloca;

import com.company.alloca.dto.EmployeeDto;
import com.company.alloca.dto.EmployeeSearchResponse;
import com.company.alloca.dto.SkillDto;
import com.company.alloca.entity.Employee;
import com.company.alloca.entity.Skill;
import com.company.alloca.exception.EmployeeNotFoundException;
import com.company.alloca.repository.EmployeeRepository;
import com.company.alloca.repository.ResourceAllocationRepository;
import com.company.alloca.repository.SkillRepository;
import com.company.alloca.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository repository;

    @Mock
    private ResourceAllocationRepository allocationRepository;

    @Mock
    private SkillRepository skillRepository;

    @InjectMocks
    private EmployeeService service;

    private Employee testEmployee;
    private EmployeeDto testDto;

    @BeforeEach
    void setUp() {
        testEmployee = new Employee();
        testEmployee.setEmployeeId(1L);
        testEmployee.setEmployeeCode("EMP001");
        testEmployee.setFullName("Tuan Ho Anh");
        testEmployee.setEmail("tuanha@company.com");
        testEmployee.setRole("Senior Developer");
        testEmployee.setDepartment("FSOFT-Q1");

        testDto = EmployeeDto.builder()
                .employeeCode("EMP001")
                .fullName("Tuan Ho Anh")
                .email("tuanha@company.com")
                .role("Senior Developer")
                .department("FSOFT-Q1")
                .build();
    }

    @Test
    void createEmployee_validData_success() {
        when(repository.save(any(Employee.class))).thenAnswer(invocation -> {
            Employee e = invocation.getArgument(0);
            e.setEmployeeId(1L);
            return e;
        });

        EmployeeDto result = service.createEmployee(testDto);

        assertNotNull(result);
        assertEquals("EMP001", result.getEmployeeCode());
        assertEquals(1L, result.getEmployeeId());
        verify(repository).save(any(Employee.class));
    }

    @Test
    void getEmployeeById_exists_returnsEmployee() {
        when(repository.findById(1L)).thenReturn(Optional.of(testEmployee));

        EmployeeDto result = service.getEmployeeById(1L);

        assertNotNull(result);
        assertEquals("Tuan Ho Anh", result.getFullName());
        assertEquals("EMP001", result.getEmployeeCode());
    }

    @Test
    void getEmployeeById_notFound_throwsException() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () -> service.getEmployeeById(99L));
    }

    @Test
    void getAllEmployees_returnsList() {
        Employee emp2 = new Employee();
        emp2.setEmployeeId(2L);
        emp2.setEmployeeCode("EMP002");
        emp2.setFullName("Nguyen Van Nam");
        emp2.setEmail("namnv@company.com");
        emp2.setRole("Developer");
        emp2.setDepartment("FSOFT-Q2");

        when(repository.findAll()).thenReturn(Arrays.asList(testEmployee, emp2));

        var result = service.getAllEmployees();

        assertEquals(2, result.size());
    }

    @Test
    void deleteEmployee_exists_success() {
        when(repository.existsById(1L)).thenReturn(true);
        when(allocationRepository.findByEmployeeEmployeeId(1L)).thenReturn(Collections.emptyList());
        doNothing().when(repository).deleteById(1L);

        assertDoesNotThrow(() -> service.deleteEmployee(1L));
        verify(repository).deleteById(1L);
    }

    @Test
    void deleteEmployee_notFound_throwsException() {
        when(repository.existsById(99L)).thenReturn(false);

        assertThrows(EmployeeNotFoundException.class, () -> service.deleteEmployee(99L));
    }

    @Test
    void deleteEmployee_hasAllocations_throwsException() {
        when(repository.existsById(1L)).thenReturn(true);
        when(allocationRepository.findByEmployeeEmployeeId(1L)).thenReturn(List.of(new com.company.alloca.entity.ResourceAllocation()));

        assertThrows(com.company.alloca.exception.EmployeeHasAllocationsException.class, () -> service.deleteEmployee(1L));
    }

    // ===== Skill Management Tests =====

    @Test
    void addSkillsToEmployee_success() {
        when(repository.findById(1L)).thenReturn(Optional.of(testEmployee));
        Skill javaSkill = Skill.builder().skillId(1L).name("Java").build();
        when(skillRepository.findByNameInIgnoreCase(List.of("Java"))).thenReturn(new ArrayList<>(List.of(javaSkill)));
        when(repository.save(any(Employee.class))).thenReturn(testEmployee);

        List<SkillDto> result = service.addSkillsToEmployee(1L, List.of("Java"));

        assertNotNull(result);
        verify(repository).save(any(Employee.class));
    }

    @Test
    void addSkillsToEmployee_createsNewSkill() {
        when(repository.findById(1L)).thenReturn(Optional.of(testEmployee));
        when(skillRepository.findByNameInIgnoreCase(List.of("NewSkill"))).thenReturn(new ArrayList<>());
        Skill newSkill = Skill.builder().skillId(99L).name("NewSkill").build();
        when(skillRepository.saveAll(anyList())).thenReturn(new ArrayList<>(List.of(newSkill)));
        when(repository.save(any(Employee.class))).thenReturn(testEmployee);

        List<SkillDto> result = service.addSkillsToEmployee(1L, List.of("NewSkill"));

        assertNotNull(result);
        verify(skillRepository).saveAll(anyList());
    }

    @Test
    void getEmployeeSkills_returnsSkillList() {
        Skill skill1 = Skill.builder().skillId(1L).name("Java").build();
        Skill skill2 = Skill.builder().skillId(2L).name("Spring Boot").build();
        testEmployee.setSkills(new HashSet<>(Set.of(skill1, skill2)));
        when(repository.findById(1L)).thenReturn(Optional.of(testEmployee));

        List<SkillDto> result = service.getEmployeeSkills(1L);

        assertEquals(2, result.size());
    }

    @Test
    void searchEmployeesBySkill_returnsResults() {
        EmployeeSearchResponse mockResponse = new EmployeeSearchResponse("Tuan Ho Anh", 60L);
        when(repository.searchEmployeesBySkillWithCapacity(eq("Java"), any(java.time.LocalDate.class))).thenReturn(List.of(mockResponse));

        List<EmployeeSearchResponse> result = service.searchEmployeesBySkill("Java");

        assertEquals(1, result.size());
        assertEquals("Tuan Ho Anh", result.get(0).getEmployeeName());
        assertEquals(40, result.get(0).getAvailable());
    }
}