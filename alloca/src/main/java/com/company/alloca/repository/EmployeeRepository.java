package com.company.alloca.repository;

import com.company.alloca.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM Employee e WHERE e.employeeId = :id")
    Optional<Employee> findWithLockByEmployeeId(@Param("id") Long id);

    Optional<Employee> findByEmployeeCode(String employeeCode);

    Optional<Employee> findByEmail(String email);

    boolean existsByEmployeeCode(String employeeCode);

    boolean existsByEmail(String email);

    @Query("SELECT e.employeeCode FROM Employee e ORDER BY e.employeeCode DESC LIMIT 1")
    Optional<String> findMaxEmployeeCode();

    @Query("SELECT e FROM Employee e JOIN e.skills s WHERE LOWER(s.name) = LOWER(:skillName)")
    List<Employee> findBySkillName(@Param("skillName") String skillName);

    @Query("SELECT new com.company.alloca.dto.EmployeeSearchResponse(e.fullName, COALESCE(SUM(ra.allocationPercent), 0L)) " +
           "FROM Employee e JOIN e.skills s LEFT JOIN e.allocations ra ON ra.status <> com.company.alloca.entity.AllocationStatus.ENDED " +
           "AND ra.startDate <= :currentDate AND ra.endDate >= :currentDate " +
           "WHERE LOWER(s.name) = LOWER(:skillName) " +
           "GROUP BY e.employeeId, e.fullName")
    List<com.company.alloca.dto.EmployeeSearchResponse> searchEmployeesBySkillWithCapacity(@Param("skillName") String skillName, @Param("currentDate") java.time.LocalDate currentDate);
}