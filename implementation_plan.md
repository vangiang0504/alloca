# Plan Review & Revised Implementation Plan — ALLOCA v1.5

## Review Summary

I performed a detailed gap analysis comparing the [requirements document](file:///C:/FSA%20Java/PRAA-main/Resource_Allocation_Management_5_Requirement.md), the existing codebase, and the original plan. Below are my findings, followed by a revised plan.

---

## Gap Analysis

### ✅ What the plan gets right

| Requirement | Plan Coverage |
|---|---|
| 2.1 Skill Management (entity, join table, APIs) | Fully covered |
| 2.2 Resource Search by skill | Fully covered |
| 2.3 Allocation Status (enum, workflow, APIs) | Fully covered |
| 2.4 Workload API response shape | Covered (adds `allocated` field) |
| 2.5 AI Review Report | Covered |
| Postman collection update | Covered |

---

### ⚠️ Issues & Improvements Found

#### Issue 1: `endAllocation` workflow is under-specified

> [!WARNING]
> The requirements say: _"Allocation ENDED không được ACTIVE lại"_ — but the plan says `endAllocation` "Transitions status to ENDED. Throws error if already ENDED."
>
> This means the plan allows ending a `PENDING` allocation directly (skipping `ACTIVE`). The requirement workflow diagram shows a linear flow `PENDING → ACTIVE → ENDED`, which implies **only `ACTIVE` allocations can be ended**.

**Fix:** `endAllocation` should only transition from `ACTIVE` → `ENDED`. Ending a `PENDING` allocation should be rejected.

---

#### Issue 2: PENDING allocations and the 100% capacity rule

> [!IMPORTANT]
> The plan says `PENDING` and `ACTIVE` allocations both count toward the 100% cap. But consider this: if a `PENDING` allocation consumes capacity, it blocks other work even before it is confirmed. This is likely the intended behavior (reserve capacity upfront), but it should be stated explicitly.

**Clarification:** `PENDING` allocations **do** count toward the 100% capacity (they reserve capacity). Only `ENDED` allocations release it. This is consistent with the plan's `IMPORTANT` note.

---

#### Issue 3: Missing `@Email` validation on EmployeeDto

> [!NOTE]
> The requirements specify `@Email` validation. The current [EmployeeDto.java](file:///C:/FSA%20Java/PRAA-main/alloca/src/main/java/com/company/alloca/dto/EmployeeDto.java) already has `@Email` — ✅ no change needed. But the plan doesn't mention verifying this.

---

#### Issue 4: Missing exception for invalid allocation status transitions

> [!WARNING]
> The plan reuses `ProjectStatusException` for allocation status transitions, but that's semantically wrong. A new `InvalidAllocationStatusException` should be created and registered in [GlobalExceptionHandler.java](file:///C:/FSA%20Java/PRAA-main/alloca/src/main/java/com/company/alloca/exception/GlobalExceptionHandler.java).

**Fix:** Add a new `InvalidAllocationStatusException` exception class and handler.

---

#### Issue 5: `EmployeeRepository` query for skill-based search is missing from the plan

> [!NOTE]
> The plan places skill search logic in `EmployeeService` but doesn't specify whether it will use a custom JPQL query in `EmployeeRepository` or navigate via the `Skill` entity. For clean architecture, a JPQL join query in the repository is preferred.

**Fix:** Add a custom `@Query` method in `EmployeeRepository` to find employees by skill name with a JOIN.

---

#### Issue 6: `WorkloadDto` field naming mismatch

The requirements sample response uses:
```json
{
  "employeeId": 1,
  "employeeName": "Nguyen Van A",
  "allocated": 70,
  "available": 30
}
```

The current [WorkloadDto.java](file:///C:/FSA%20Java/PRAA-main/alloca/src/main/java/com/company/alloca/dto/WorkloadDto.java) has `totalAllocation` instead of `allocated`. The plan says "add `allocated` field" but doesn't mention **renaming** `totalAllocation` → `allocated`.

**Fix:** Rename the field `totalAllocation` → `allocated` to match the required API response exactly.

---

#### Issue 7: `DELETE /projects/{id}` is in the current code but NOT in the v1.5 requirements

The requirements spec for Project Management (Section 1.2) lists only:
```http
POST /projects
GET /projects
GET /projects/{id}
PUT /projects/{id}
```

The current [ProjectController.java](file:///C:/FSA%20Java/PRAA-main/alloca/src/main/java/com/company/alloca/controller/ProjectController.java) has a `DELETE /projects/{id}` endpoint. This is an **extra** endpoint not in the requirements. It's harmless to keep, so no action needed — but worth noting.

---

## Revised Implementation Plan

### 1. Entity & Database Layer

#### [NEW] `AllocationStatus.java`
- Enum: `PENDING`, `ACTIVE`, `ENDED`

#### [NEW] `Skill.java`
- Entity mapped to `skill` table
- Fields: `skillId` (PK, auto), `name` (unique, not-null, length 100)
- **Unidirectional** from Employee side (Employee owns the relationship; Skill does not need a back-reference to Employee)

#### [MODIFY] `Employee.java`
- Add `@ManyToMany` with `@JoinTable(name = "employee_skill", joinColumns = @JoinColumn(name = "employee_id"), inverseJoinColumns = @JoinColumn(name = "skill_id"))`
- Add `Set<Skill> skills` field with getter/setter

#### [MODIFY] `ResourceAllocation.java`
- Add `@Enumerated(EnumType.STRING) @Column(name = "status") AllocationStatus status`
- Default to `PENDING` via `@Builder.Default`

#### [MODIFY] `data.sql`
- Add `status` column to all existing `INSERT INTO allocation` statements (set to `'ACTIVE'` for seed data)
- Add `INSERT INTO skill` rows (Java, Spring Boot, PostgreSQL, React, Python, etc.)
- Add `INSERT INTO employee_skill` rows mapping employees to skills

---

### 2. Repository Layer

#### [NEW] `SkillRepository.java`
- `Optional<Skill> findByNameIgnoreCase(String name)`

#### [MODIFY] `EmployeeRepository.java`
- Add `@Query` method: find employees by skill name using JPQL join on `employee_skill`

#### [MODIFY] `ResourceAllocationRepository.java`
- Update `sumAllocationByEmployeeId` → filter `ra.status <> 'ENDED'`
- Update `getUtilizationReport` → filter `ra.status <> 'ENDED'`
- Update `getAvailableResources` → filter `ra.status <> 'ENDED'`
- Update `getOverloadedEmployees` → filter `ra.status <> 'ENDED'`

---

### 3. DTO Layer

#### [NEW] `SkillDto.java`
- Fields: `skillId`, `name`

#### [NEW] `EmployeeSearchResponse.java`
- Fields: `employeeName`, `available` (matching the exact API spec)

#### [MODIFY] `AllocationResponse.java`
- Add `String status` field

#### [MODIFY] `WorkloadDto.java`
- **Rename** `totalAllocation` → `allocated` to match the API spec exactly

---

### 4. Exception Layer

#### [NEW] `InvalidAllocationStatusException.java`
- For invalid status transitions (e.g., activating an `ENDED` allocation)

#### [MODIFY] `GlobalExceptionHandler.java`
- Add handler for `InvalidAllocationStatusException` → return `400 BAD_REQUEST`

---

### 5. Service Layer

#### [MODIFY] `EmployeeService.java`
- `addSkillsToEmployee(Long employeeId, List<String> skillNames)` — creates skills if they don't exist, then associates
- `getEmployeeSkills(Long employeeId)` — returns List<SkillDto>
- `searchEmployeesBySkill(String skillName)` — returns List<EmployeeSearchResponse> with available capacity

#### [MODIFY] `AllocationService.java`
- `createAllocation` → set status = `PENDING`
- `activateAllocation(Long id)` → only `PENDING` → `ACTIVE` transition allowed
- `endAllocation(Long id)` → only `ACTIVE` → `ENDED` transition allowed
- Update `toResponse()` to include `status`
- Update `getEmployeeWorkload()` to use renamed `allocated` field

---

### 6. Controller Layer

#### [MODIFY] `EmployeeController.java`
- `POST /employees/{id}/skills` → body: list of skill names
- `GET /employees/{id}/skills` → returns skill list
- `GET /employees/search?skill={name}` → returns EmployeeSearchResponse list

#### [MODIFY] `AllocationController.java`
- `PUT /allocations/{id}/activate`
- `PUT /allocations/{id}/end`

---

### 7. Deliverables & Documentation

#### [NEW] `AI_Review_Report.md`
- Prompts, AI feedback, improvements applied

#### [MODIFY] `ALLOCA.postman_collection.json`
- Add requests for all new endpoints

---

## Verification Plan

### Automated Tests
- `mvn clean test`
- Update [AllocationServiceTest.java](file:///C:/FSA%20Java/PRAA-main/alloca/src/test/java/com/company/alloca/AllocationServiceTest.java):
  - Test `PENDING` → `ACTIVE` activation succeeds
  - Test `ACTIVE` → `ENDED` end succeeds
  - Test `ENDED` → `ACTIVE` activation is rejected
  - Test `PENDING` → `ENDED` end is rejected
  - Test `createAllocation` sets status to `PENDING`
- Update [EmployeeServiceTest.java](file:///C:/FSA%20Java/PRAA-main/alloca/src/test/java/com/company/alloca/EmployeeServiceTest.java):
  - Test add skills to employee
  - Test get employee skills
  - Test search employees by skill

### Manual Verification
- Swagger UI at `http://localhost:8080/swagger-ui.html`
- Postman collection
