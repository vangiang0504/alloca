# Resource Allocation Management System v1.5

## Project Overview

### Mục tiêu

Xây dựng hệ thống quản lý phân bổ nhân sự cho công ty outsourcing.

Hệ thống cho phép:

- Quản lý nhân viên
- Quản lý dự án
- Phân bổ nhân viên vào dự án
- Theo dõi workload
- Quản lý skill của nhân viên
- Tìm kiếm resource theo skill

Thời gian thực hiện: **1 ngày (6-8 giờ)**

---

# 1. Functional Requirements

## 1.1 Employee Management

### Thông tin nhân viên

- Employee Code
- Full Name
- Email
- Department
- Role

### APIs

```http
POST /employees
GET /employees
GET /employees/{id}
PUT /employees/{id}
DELETE /employees/{id}
```

---

## 1.2 Project Management

### Thông tin dự án

- Project Code
- Project Name
- Customer
- Status

### Status

```text
PLANNING
ACTIVE
COMPLETED
```

### APIs

```http
POST /projects
GET /projects
GET /projects/{id}
PUT /projects/{id}
```

---

## 1.3 Allocation Management

### Thông tin Allocation

- Employee
- Project
- Allocation Percent
- Start Date
- End Date

### Business Rule 1

```text
0 < Allocation <= 100
```

### Business Rule 2

Tổng allocation của một nhân viên không được vượt quá 100%.

Ví dụ:

```text
Project A : 60%
Project B : 40%

=> Hợp lệ
```

```text
Project A : 60%
Project B : 50%

=> Reject
```

### APIs

```http
POST /allocations
PUT /allocations/{id}
DELETE /allocations/{id}
GET /employees/{id}/workload
```

---

# 2. Nâng Cấp Version 1.5

## 2.1 Skill Management

### Mục tiêu

Cho phép quản lý skill của nhân viên.

### Ví dụ

```text
Employee: Nguyen Van A

Skills:
- Java
- Spring Boot
- PostgreSQL
```

### Database

```text
employee
skill
employee_skill
```

### APIs

```http
POST /employees/{id}/skills
GET /employees/{id}/skills
```

### Kiến thức luyện tập

- Many-to-Many Relationship
- Join Table
- JPA Mapping

---

## 2.2 Resource Search

### Mục tiêu

Tìm nhân viên theo skill.

### API

```http
GET /employees/search?skill=Java
```

### Sample Response

```json
[
  {
    "employeeName": "Nguyen Van A",
    "available": 40
  }
]
```

### Kiến thức luyện tập

- Join Query
- Filtering
- Dynamic Search

---

## 2.3 Allocation Status

### Status

```text
PENDING
ACTIVE
ENDED
```

### Workflow

```text
Create Allocation
        ↓
     PENDING
        ↓
     ACTIVE
        ↓
      ENDED
```

### APIs

```http
PUT /allocations/{id}/activate
PUT /allocations/{id}/end
```

### Validation

- Chỉ allocation có trạng thái PENDING mới được ACTIVE.
- Allocation ENDED không được ACTIVE lại.

### Kiến thức luyện tập

- State Management
- Business Validation

---

## 2.4 Available Capacity

### Mục tiêu

Hiển thị phần trăm allocation và capacity còn lại của nhân viên.

### API

```http
GET /employees/{id}/workload
```

### Sample Response

```json
{
  "employeeId": 1,
  "employeeName": "Nguyen Van A",
  "allocated": 70,
  "available": 30
}
```

### Công thức

```text
Available = 100 - Total Allocation
```

### Kiến thức luyện tập

- Aggregate Query
- Business Calculation

---

## 2.5 AI Review Report (Bonus)

Không yêu cầu tích hợp OpenAI/Gemini API.

Yêu cầu sử dụng AI để hỗ trợ phát triển.

### Generate Test Cases

```text
Generate test cases for allocation API.
```

### Review Service Layer

```text
Review this service class as Senior Java Engineer.
```

### Generate API Documentation

```text
Generate API documentation based on controller code.
```

### Deliverable

Nộp file:

```text
AI_Review_Report.md
```

Bao gồm:

- Prompt đã sử dụng
- AI feedback
- Các cải tiến đã thực hiện

---

# 3. Database Design

## Tables

```text
employee
project
allocation
skill
employee_skill
```

---

# 4. Technical Requirements

## Backend

- Java 17+
- Spring Boot
- Spring Data JPA
- PostgreSQL
- Maven

## Validation

- @NotBlank
- @Email
- @Min
- @Max

## Exception Handling

- EmployeeNotFoundException
- ProjectNotFoundException
- AllocationExceededException
- Global Exception Handler

---

# 5. Deliverables

- Source Code
- SQL Script
- Postman Collection
- README.md
- AI_Review_Report.md

---

# 6. Acceptance Criteria

✅ Employee CRUD

✅ Project CRUD

✅ Allocation CRUD

✅ Allocation ≤ 100%

✅ Skill Management

✅ Search Employee By Skill

✅ Allocation Status Workflow

✅ Workload API

✅ Validation

✅ Global Exception Handling

✅ README & SQL Script

---



# Learning Objectives

Sau khi hoàn thành bài tập, cần hiểu được:

- Java OOP
- Spring Boot CRUD
- REST API Design
- Database Relationship
- SQL JOIN
- JPA Mapping
- Business Validation
- Exception Handling
- AI-assisted Development
