# ALLOCA - Project Resource Allocation Management System

## Overview
ALLOCA is a full-stack project resource allocation management system designed for outsourcing or multi-project organizations. It helps teams manage employees, projects, allocations, workload visibility, reporting, and AI-assisted planning.

The project includes:
- `alloca/` - Spring Boot backend
- `alloca-frontend/` - React + Vite frontend
- `docker-compose.yml` - local infrastructure setup for PostgreSQL and backend

## Tech Stack

### Backend
- Java 17
- Spring Boot 3
- Spring Web
- Spring Data JPA
- PostgreSQL
- SpringDoc OpenAPI / Swagger UI
- Maven

### Frontend
- React
- Vite
- Material UI
- Axios
- Framer Motion
- Recharts

## Main Features
- Employee management (CRUD)
- Project management (CRUD)
- Resource allocation across multiple projects
- Employee workload tracking
- Reports for utilization, availability, and overloaded employees
- AI-assisted recommendations and risk detection
- Swagger API documentation
- Sample seed data via `data.sql`

## Project Structure

```text
PRAA-main/
├── alloca/               # Spring Boot backend
├── alloca-frontend/      # React frontend
├── docker-compose.yml
└── README.md
```

## Prerequisites
- Java 17+
- Maven 3.9+
- Node.js 18+
- npm
- PostgreSQL
- Docker and Docker Compose (optional)

## Running the Backend

### Option 1: Run locally
1. Create a PostgreSQL database named `alloca_db`.
2. Update credentials in `alloca/src/main/resources/application.properties` if needed.
3. Start the backend:

```bash
cd alloca
mvn clean install
mvn spring-boot:run
```

Backend will run at:
- `http://localhost:8080`

Swagger UI:
- `http://localhost:8080/swagger-ui.html`

OpenAPI JSON:
- `http://localhost:8080/api-docs`

## Running the Frontend
1. Install dependencies:

```bash
cd alloca-frontend
npm install
```

2. Start the dev server:

```bash
npm run dev
```

Frontend will run at:
- `http://localhost:5173`

3. Production build:

```bash
npm run build
```

## Running with Docker Compose
You can start PostgreSQL and the backend with Docker Compose:

```bash
docker compose up --build
```

Services:
- PostgreSQL: `localhost:5437`
- Backend: `http://localhost:8080`

The frontend should still be run separately from `alloca-frontend/`.

## Configuration

### Backend database config
File:
- `alloca/src/main/resources/application.properties`

Default values:
- Database: `alloca_db`
- Username: `postgres`
- Password: `1`
- JDBC URL: `jdbc:postgresql://localhost:5437/alloca_db`

### Frontend API base URL
File:
- `alloca-frontend/src/api/api.js`

Default value:
```js
baseURL: 'http://localhost:8080'
```

## API Summary

### Employees
- `GET /employees`
- `GET /employees/{id}`
- `POST /employees`
- `PUT /employees/{id}`
- `DELETE /employees/{id}`

### Projects
- `GET /projects`
- `GET /projects/{id}`
- `POST /projects`
- `PUT /projects/{id}`
- `DELETE /projects/{id}`

### Allocations
- `GET /allocations`
- `POST /allocations`
- `PUT /allocations/{id}`
- `DELETE /allocations/{id}`
- `GET /employees/{id}/workload`

### Reports
- `GET /reports/utilization`
- `GET /reports/available`
- `GET /reports/overloaded`

### AI
- `POST /ai/recommend`
- `POST /ai/risk-detection`

## Sample Request Payloads

### Create employee
```json
{
  "fullName": "John Doe",
  "email": "john.doe@company.com",
  "role": "Backend Developer",
  "department": "Engineering"
}
```

### Create project
```json
{
  "projectCode": "ALC001",
  "projectName": "Allocation Portal",
  "customer": "Internal",
  "status": "ACTIVE",
  "startDate": "2026-07-01",
  "endDate": "2026-12-31"
}
```

### Create allocation
```json
{
  "employeeId": 1,
  "projectId": 1,
  "allocationPercent": 50,
  "roleInProject": "Backend Developer",
  "startDate": "2026-07-01",
  "endDate": "2026-12-31"
}
```

### AI recommend
```json
{
  "role": "Developer",
  "minAvailable": 50,
  "count": 3
}
```

### AI risk detection
```json
{
  "neededResources": 2,
  "role": "Developer"
}
```

## Testing and Validation

### Backend
```bash
cd alloca
mvn test
```

### Frontend
```bash
cd alloca-frontend
npm run build
```

## Notes
- The frontend contains mock fallback behavior in `src/api/api.js` for some endpoints when the backend is offline.
- Swagger UI can be used to inspect and test backend APIs interactively.
- A Postman collection is included in the repository for quick API testing.

## Deliverables
- Full backend source code in `alloca/`
- Full frontend source code in `alloca-frontend/`
- English project documentation in `README.md`
- Postman collection for API testing

---
ALLOCA is intended as a practical sample full-stack project for resource planning, allocation visibility, and reporting in a multi-project environment.
