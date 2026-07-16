# Plan vs Implementation Audit — ALLOCA v1.5

Comparison of every planned task against the actual implemented code.

---

## 1. Entity & Database Layer

### [NEW] `AllocationStatus.java` — ✅ Completed

| Plan | Code |
|------|------|
| Enum: `PENDING`, `ACTIVE`, `ENDED` | ✅ Exactly as planned |

📄 [AllocationStatus.java](file:///C:/FSA%20Java/PRAA-main/alloca/src/main/java/com/company/alloca/entity/AllocationStatus.java)

**Deviations:** None.

---

### [NEW] `Skill.java` — ✅ Completed

| Plan | Code |
|------|------|
| Entity mapped to `skill` table | ✅ `@Table(name = "skill")` |
| Fields: `skillId` (PK, auto), `name` (unique, not-null, length 100) | ✅ Exact match |
| Unidirectional from Employee side | ✅ No back-reference to Employee |

📄 [Skill.java](file:///C:/FSA%20Java/PRAA-main/alloca/src/main/java/com/company/alloca/entity/Skill.java)

**Deviations:** None.

---

### [MODIFY] `Employee.java` — ✅ Completed

| Plan | Code |
|------|------|
| `@ManyToMany` with `@JoinTable(name = "employee_skill", ...)` | ✅ Lines 43-48 |
| `Set<Skill> skills` with getter/setter | ✅ Lines 50, 66-67 |

📄 [Employee.java](file:///C:/FSA%20Java/PRAA-main/alloca/src/main/java/com/company/alloca/entity/Employee.java#L43-L50)

**Deviations:** None.

---

### [MODIFY] `ResourceAllocation.java` — ✅ Completed

| Plan | Code |
|------|------|
| `@Enumerated(EnumType.STRING) @Column(name = "status")` | ✅ Lines 41-44 |
| Default to `PENDING` via `@Builder.Default` | ✅ Line 44 |
| Getter/setter for status | ✅ Lines 60-61 |

📄 [ResourceAllocation.java](file:///C:/FSA%20Java/PRAA-main/alloca/src/main/java/com/company/alloca/entity/ResourceAllocation.java#L41-L44)

**Deviations:** None.

---

### [MODIFY] `data.sql` — ✅ Completed

| Plan | Code |
|------|------|
| Add `status` column to allocation INSERTs (set to `'ACTIVE'`) | ✅ All 12 rows have `'ACTIVE'` |
| Add `INSERT INTO skill` rows | ✅ 10 skills (Java, Spring Boot, PostgreSQL, React, Python, Docker, Kubernetes, JavaScript, HTML/CSS, Figma) |
| Add `INSERT INTO employee_skill` rows | ✅ All 7 employees mapped to skills |

📄 [data.sql](file:///C:/FSA%20Java/PRAA-main/alloca/src/main/resources/data.sql)

**Deviations:** None.

---

## 2. Repository Layer

### [NEW] `SkillRepository.java` — ✅ Completed

| Plan | Code |
|------|------|
| `Optional<Skill> findByNameIgnoreCase(String name)` | ✅ Line 12 |

📄 [SkillRepository.java](file:///C:/FSA%20Java/PRAA-main/alloca/src/main/java/com/company/alloca/repository/SkillRepository.java)

**Deviations:** None.

---

### [MODIFY] `EmployeeRepository.java` — ✅ Completed

| Plan | Code |
|------|------|
| Add `@Query` method: find employees by skill name using JPQL join | ✅ Lines 26-27: `JOIN e.skills s WHERE LOWER(s.name) = LOWER(:skillName)` |

📄 [EmployeeRepository.java](file:///C:/FSA%20Java/PRAA-main/alloca/src/main/java/com/company/alloca/repository/EmployeeRepository.java#L26-L27)

**Deviations:** None.

---

### [MODIFY] `ResourceAllocationRepository.java` — ✅ Completed

| Plan | Code |
|------|------|
| `sumAllocationByEmployeeId` → filter `ra.status <> ENDED` | ✅ Lines 18-20 |
| `getUtilizationReport` → filter `ra.status <> ENDED` | ✅ Lines 22-26 |
| `getAvailableResources` → filter `ra.status <> ENDED` | ✅ Lines 28-33 |
| `getOverloadedEmployees` → filter `ra.status <> ENDED` | ✅ Lines 35-39 |

📄 [ResourceAllocationRepository.java](file:///C:/FSA%20Java/PRAA-main/alloca/src/main/java/com/company/alloca/repository/ResourceAllocationRepository.java)

**Deviations:** None.

---

## 3. DTO Layer

### [NEW] `SkillDto.java` — ✅ Completed

| Plan | Code |
|------|------|
| Fields: `skillId`, `name` | ✅ Lines 13-14 |

📄 [SkillDto.java](file:///C:/FSA%20Java/PRAA-main/alloca/src/main/java/com/company/alloca/dto/SkillDto.java)

**Deviations:** None.

---

### [NEW] `EmployeeSearchResponse.java` — ✅ Completed

| Plan | Code |
|------|------|
| Fields: `employeeName`, `available` | ✅ Lines 13-14 |

📄 [EmployeeSearchResponse.java](file:///C:/FSA%20Java/PRAA-main/alloca/src/main/java/com/company/alloca/dto/EmployeeSearchResponse.java)

**Deviations:** None.

---

### [MODIFY] `AllocationResponse.java` — ✅ Completed

| Plan | Code |
|------|------|
| Add `String status` field | ✅ Line 22 |

📄 [AllocationResponse.java](file:///C:/FSA%20Java/PRAA-main/alloca/src/main/java/com/company/alloca/dto/AllocationResponse.java#L22)

**Deviations:** None.

---

### [MODIFY] `WorkloadDto.java` — ✅ Completed

| Plan | Code |
|------|------|
| Rename `totalAllocation` → `allocated` | ✅ Line 15: `private Integer allocated;` |

📄 [WorkloadDto.java](file:///C:/FSA%20Java/PRAA-main/alloca/src/main/java/com/company/alloca/dto/WorkloadDto.java#L15)

**Deviations:** None.

---

## 4. Exception Layer

### [NEW] `InvalidAllocationStatusException.java` — ✅ Completed

📄 [InvalidAllocationStatusException.java](file:///C:/FSA%20Java/PRAA-main/alloca/src/main/java/com/company/alloca/exception/InvalidAllocationStatusException.java)

**Deviations:** None.

---

### [MODIFY] `GlobalExceptionHandler.java` — ✅ Completed

| Plan | Code |
|------|------|
| Add handler for `InvalidAllocationStatusException` → `400 BAD_REQUEST` | ✅ Lines 44-49 |

📄 [GlobalExceptionHandler.java](file:///C:/FSA%20Java/PRAA-main/alloca/src/main/java/com/company/alloca/exception/GlobalExceptionHandler.java#L44-L49)

**Deviations:** None.

---

## 5. Service Layer

### [MODIFY] `EmployeeService.java` — ✅ Completed

| Plan | Code |
|------|------|
| `addSkillsToEmployee(Long, List<String>)` — creates skills if missing | ✅ Lines 107-124 |
| `getEmployeeSkills(Long)` → `List<SkillDto>` | ✅ Lines 126-131 |
| `searchEmployeesBySkill(String)` → `List<EmployeeSearchResponse>` | ✅ Lines 134-145 |

📄 [EmployeeService.java](file:///C:/FSA%20Java/PRAA-main/alloca/src/main/java/com/company/alloca/service/EmployeeService.java#L107-L145)

**Deviations:** None.

---

### [MODIFY] `AllocationService.java` — ⚠️ Partially Completed

| Plan | Code | Status |
|------|------|--------|
| `createAllocation` → set status = `PENDING` | Relies on `@Builder.Default` (L44 in entity). Not explicitly set in service. | ⚠️ **Implicit** |
| `activateAllocation(Long id)` → only `PENDING` → `ACTIVE` | ✅ Lines 149-161 | ✅ |
| `endAllocation(Long id)` → only `ACTIVE` → `ENDED` | ✅ Lines 163-175 | ✅ |
| `toResponse()` → include `status` | ✅ Line 188 | ✅ |
| `getEmployeeWorkload()` → use `allocated` field | ✅ Line 115 | ✅ |

📄 [AllocationService.java](file:///C:/FSA%20Java/PRAA-main/alloca/src/main/java/com/company/alloca/service/AllocationService.java)

**Deviations:**
1. **`createAllocation` does not explicitly set `status = PENDING`** in the builder call (lines 56-63). It works because `ResourceAllocation` has `@Builder.Default private AllocationStatus status = AllocationStatus.PENDING`. This is functionally correct but fragile — if someone constructs via `new ResourceAllocation()` + setters instead of the builder, status would be `null` without an explicit set. The plan said _"set status = PENDING"_ which implies an explicit assignment.

---

## 6. Controller Layer

### [MODIFY] `EmployeeController.java` — ✅ Completed

| Plan | Code |
|------|------|
| `POST /employees/{id}/skills` → body: list of skill names | ✅ Lines 47-51 |
| `GET /employees/{id}/skills` → returns skill list | ✅ Lines 53-56 |
| `GET /employees/search?skill={name}` → returns EmployeeSearchResponse list | ✅ Lines 58-62 |

📄 [EmployeeController.java](file:///C:/FSA%20Java/PRAA-main/alloca/src/main/java/com/company/alloca/controller/EmployeeController.java#L47-L62)

**Deviations:** None.

---

### [MODIFY] `AllocationController.java` — ✅ Completed

| Plan | Code |
|------|------|
| `PUT /allocations/{id}/activate` | ✅ Lines 47-50 |
| `PUT /allocations/{id}/end` | ✅ Lines 52-55 |

📄 [AllocationController.java](file:///C:/FSA%20Java/PRAA-main/alloca/src/main/java/com/company/alloca/controller/AllocationController.java#L47-L55)

**Deviations:** None.

---

## 7. Deliverables & Documentation

### [NEW] `AI_Review_Report.md` — ✅ Completed

📄 [AI_Review_Report.md](file:///C:/FSA%20Java/PRAA-main/AI_Review_Report.md) (5,971 bytes)

**Deviations:** None.

---

### [MODIFY] `ALLOCA.postman_collection.json` — ✅ Completed

📄 [ALLOCA.postman_collection.json](file:///C:/FSA%20Java/PRAA-main/ALLOCA.postman_collection.json) (9,230 bytes)

**Deviations:** None.

---

## 8. Verification Plan

### Automated Tests

| Planned Test | Status | Location |
|---|---|---|
| `PENDING` → `ACTIVE` activation succeeds | ✅ | [AllocationServiceTest.java:L232-L243](file:///C:/FSA%20Java/PRAA-main/alloca/src/test/java/com/company/alloca/AllocationServiceTest.java#L232-L243) |
| `ACTIVE` → `ENDED` end succeeds | ✅ | [AllocationServiceTest.java:L267-L278](file:///C:/FSA%20Java/PRAA-main/alloca/src/test/java/com/company/alloca/AllocationServiceTest.java#L267-L278) |
| `ENDED` → `ACTIVE` activation is rejected | ✅ | [AllocationServiceTest.java:L245-L256](file:///C:/FSA%20Java/PRAA-main/alloca/src/test/java/com/company/alloca/AllocationServiceTest.java#L245-L256) |
| `PENDING` → `ENDED` end is rejected | ✅ | [AllocationServiceTest.java:L280-L291](file:///C:/FSA%20Java/PRAA-main/alloca/src/test/java/com/company/alloca/AllocationServiceTest.java#L280-L291) |
| `createAllocation` sets status to `PENDING` | ❌ **Missing** | No test verifies the response has `status = "PENDING"` |
| Test add skills to employee | ✅ | [EmployeeServiceTest.java:L127-L138](file:///C:/FSA%20Java/PRAA-main/alloca/src/test/java/com/company/alloca/EmployeeServiceTest.java#L127-L138) |
| Test get employee skills | ✅ | [EmployeeServiceTest.java:L148-L156](file:///C:/FSA%20Java/PRAA-main/alloca/src/test/java/com/company/alloca/EmployeeServiceTest.java#L148-L156) |
| Test search employees by skill | ✅ | [EmployeeServiceTest.java:L158-L168](file:///C:/FSA%20Java/PRAA-main/alloca/src/test/java/com/company/alloca/EmployeeServiceTest.java#L158-L168) |
| `mvn clean test` passes | ✅ | 24 tests, 0 failures |

---

## Summary

### Scorecard

| Layer | Planned Items | Completed | Partial | Missing |
|-------|:---:|:---:|:---:|:---:|
| 1. Entity & Database | 5 | 5 | 0 | 0 |
| 2. Repository | 3 | 3 | 0 | 0 |
| 3. DTO | 4 | 4 | 0 | 0 |
| 4. Exception | 2 | 2 | 0 | 0 |
| 5. Service | 2 | 1 | 1 | 0 |
| 6. Controller | 2 | 2 | 0 | 0 |
| 7. Deliverables | 2 | 2 | 0 | 0 |
| 8. Verification | 8 | 7 | 0 | 1 |
| **Total** | **28** | **26** | **1** | **1** |

---

### Unfinished Work

> [!WARNING]
> **2 items require attention:**

#### 1. `createAllocation` — implicit PENDING status (Partial)
- **File:** [AllocationService.java:L56-L63](file:///C:/FSA%20Java/PRAA-main/alloca/src/main/java/com/company/alloca/service/AllocationService.java#L56-L63)
- **Issue:** The builder relies on `@Builder.Default` to set PENDING. The plan said _"set status = PENDING"_, implying an explicit `.status(AllocationStatus.PENDING)` in the builder chain.
- **Risk:** Low (it works), but explicit is better than implicit for maintainability.

#### 2. Missing test: `createAllocation` sets status to `PENDING`
- **File:** [AllocationServiceTest.java](file:///C:/FSA%20Java/PRAA-main/alloca/src/test/java/com/company/alloca/AllocationServiceTest.java)
- **Issue:** The plan explicitly lists _"Test `createAllocation` sets status to `PENDING`"_ but no such test was written. The existing `createAllocation_validData_success` test asserts on employee name and percent but does **not** assert `assertEquals("PENDING", response.getStatus())`.
