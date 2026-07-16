-- ============================================================
-- Project Resource Allocation Management System (ALLOCA) v1.5
-- Sample Test Data
-- ============================================================

-- EMPLOYEES
INSERT INTO employee (employee_code, full_name, email, role, department) VALUES
('EMP001', 'Tuan Ho Anh', 'tuanha@company.com', 'Senior Developer', 'FSOFT-Q1'),
('EMP002', 'Nguyen Van Nam', 'namnv@company.com', 'Developer', 'FSOFT-Q1'),
('EMP003', 'Tran Thi Lan', 'lanti@company.com', 'QA Engineer', 'FSOFT-Q2'),
('EMP004', 'Le Minh Duc', 'duclm@company.com', 'Tech Lead', 'FSOFT-Q1'),
('EMP005', 'Pham Thi Huong', 'huongpt@company.com', 'Business Analyst', 'FSOFT-Q3'),
('EMP006', 'Hoang Van Khoa', 'khoahv@company.com', 'Junior Developer', 'FSOFT-Q2'),
('EMP007', 'Dang Ngoc Anh', 'anhnd@company.com', 'Designer', 'FSOFT-Q3');

-- PROJECTS
INSERT INTO project (project_code, project_name, customer, status, start_date, end_date) VALUES
('NCG', 'Next Generation Commerce Platform', 'Japan Bank Corp', 'ACTIVE', '2026-01-01', '2026-12-31'),
('GRID', 'Grid Management System', 'US Energy Co', 'ACTIVE', '2026-03-01', '2026-09-30'),
('INT-AI', 'Internal AI Assistant', 'FSOFT Internal', 'ACTIVE', '2026-06-01', '2026-11-30'),
('LEGACY', 'Legacy System Maintenance', 'Viettel', 'COMPLETED', '2025-01-01', '2026-03-31'),
('NEO', 'Neo Banking Portal', 'Techcombank', 'PLANNING', '2026-08-01', '2027-06-30'),
('HEALTH', 'Healthcare Mobile App', 'Vinmec', 'PLANNING', '2026-09-01', '2027-03-31');

-- SKILLS
INSERT INTO skill (name) VALUES
('Java'),
('Spring Boot'),
('PostgreSQL'),
('React'),
('Python'),
('Docker'),
('Kubernetes'),
('JavaScript'),
('HTML/CSS'),
('Figma');

-- EMPLOYEE_SKILL (Many-to-Many mapping)
INSERT INTO employee_skill (employee_id, skill_id) VALUES
-- Tuan: Java, Spring Boot, PostgreSQL, Docker
(1, 1), (1, 2), (1, 3), (1, 6),
-- Nam: Java, Spring Boot, React, JavaScript
(2, 1), (2, 2), (2, 4), (2, 8),
-- Lan: Java, PostgreSQL
(3, 1), (3, 3),
-- Duc: Java, Spring Boot, PostgreSQL, Docker, Kubernetes
(4, 1), (4, 2), (4, 3), (4, 6), (4, 7),
-- Huong: Python, PostgreSQL
(5, 5), (5, 3),
-- Khoa: Java, React, JavaScript, HTML/CSS
(6, 1), (6, 4), (6, 8), (6, 9),
-- Anh: Figma, HTML/CSS, React
(7, 10), (7, 9), (7, 4);

-- ALLOCATIONS (valid: total per employee <= 100%, with status)
INSERT INTO allocation (employee_id, project_id, allocation_percent, role_in_project, start_date, end_date, status) VALUES
-- Tuan: NCG 60% + GRID 30% + INT-AI 10% = 100%
(1, 1, 60, 'Backend Developer', '2026-07-01', '2026-07-31', 'ACTIVE'),
(1, 2, 30, 'Architect', '2026-07-01', '2026-07-31', 'ACTIVE'),
(1, 3, 10, 'Mentor', '2026-07-01', '2026-07-31', 'ACTIVE'),

-- Nam: GRID 50% + NCG 40% = 90%
(2, 2, 50, 'Fullstack Developer', '2026-07-01', '2026-07-31', 'ACTIVE'),
(2, 1, 40, 'Frontend Developer', '2026-07-01', '2026-07-31', 'ACTIVE'),

-- Lan: NCG 40% = 40% (available 60%)
(3, 1, 40, 'QA Engineer', '2026-07-01', '2026-07-31', 'ACTIVE'),

-- Duc: NCG 30% + GRID 50% + INT-AI 20% = 100%
(4, 1, 30, 'Tech Lead', '2026-07-01', '2026-07-31', 'ACTIVE'),
(4, 2, 50, 'Solution Architect', '2026-07-01', '2026-07-31', 'ACTIVE'),
(4, 3, 20, 'Tech Lead', '2026-07-01', '2026-07-31', 'ACTIVE'),

-- Huong: NCG 50% = 50% (available 50%)
(5, 1, 50, 'Business Analyst', '2026-07-01', '2026-07-31', 'ACTIVE'),

-- Khoa: GRID 30% = 30% (available 70%)
(6, 2, 30, 'Junior Developer', '2026-07-01', '2026-07-31', 'ACTIVE'),

-- Anh: INT-AI 25% = 25% (available 75%)
(7, 3, 25, 'UI/UX Designer', '2026-07-01', '2026-07-31', 'ACTIVE');