# Project Structure

## At a Glance

This repository is organized as a Spring Boot backend plus two UI options:
- A built-in static demo UI served by the backend
- An Angular drop-in module for integration into an existing Angular app

## Repository Layout

```text
activity-audit-traceability-main/
|-- backend/
|   |-- pom.xml
|   |-- Dockerfile
|   `-- src/
|       |-- main/
|       |   |-- java/com/hackathon/audit/
|       |   |   |-- ActivityAuditApplication.java
|       |   |   |-- api/
|       |   |   |   `-- AuditLogController.java
|       |   |   |-- auditing/
|       |   |   |   |-- Auditable.java
|       |   |   |   |-- AuditAspect.java
|       |   |   |   |-- AuditLog.java
|       |   |   |   |-- AuditLogRepository.java
|       |   |   |   |-- AuditLogSpecs.java
|       |   |   |   |-- IdExtractor.java
|       |   |   |   |-- EntityIdExtractor.java
|       |   |   |   |-- ReflectionIdExtractor.java
|       |   |   |   `-- RedactionUtil.java
|       |   |   |-- config/
|       |   |   |   |-- SecurityConfig.java
|       |   |   |   `-- WebSocketConfig.java
|       |   |   `-- demo/
|       |   |       |-- DemoOrderController.java
|       |   |       |-- DemoAdminController.java
|       |   |       |-- DemoDataService.java
|       |   |       |-- Order.java
|       |   |       |-- OrderDto.java
|       |   |       |-- OrderStore.java
|       |   |       `-- SeedDataRunner.java
|       |   `-- resources/
|       |       |-- application.yml
|       |       |-- application-local.yml
|       |       `-- static/
|       |           |-- index.html
|       |           |-- app.js
|       |           `-- styles.css
|       `-- test/java/com/hackathon/audit/
|           |-- api/AuditAspectIntegrationTest.java
|           `-- auditing/
|               |-- AuditLogSpecsJpaTest.java
|               `-- RedactionUtilTest.java
|-- frontend-dropin/
|   |-- package.json
|   `-- src/app/audit/
|       |-- audit-log.component.ts
|       |-- audit-log.component.html
|       |-- audit-log.component.scss
|       |-- audit-log.model.ts
|       |-- audit-log.service.ts
|       `-- index.ts
|-- demo-ui/
|   |-- Dockerfile
|   |-- index.html
|   |-- app.js
|   `-- styles.css
|-- docker-compose.yml
|-- README.md
|-- ARCHITECTURE.md
|-- HACKATHON_REQUIREMENTS.md
|-- IMPLEMENTATION_SUMMARY.md
`-- NEW_FEATURES_GUIDE.md
```

## Backend Package Responsibilities

| Package | Purpose |
|---|---|
| `api` | Read/search/export audit logs for authorized users |
| `auditing` | Annotation, AOP interception, redaction, ID extraction, persistence model |
| `config` | Security and WebSocket/STOMP infrastructure |
| `demo` | Demo business endpoints and demo data/admin operations |

## Frontend Options

| Path | Purpose | Typical Use |
|---|---|---|
| `backend/src/main/resources/static` | Built-in demo dashboard | Fast local/cloud demo |
| `frontend-dropin/src/app/audit` | Angular reusable module | Integrate into an existing Angular app |
| `demo-ui` | Lightweight standalone demo container | Docker-compose showcase |

## Key Files for Judges

| Requirement Area | Primary Files |
|---|---|
| Audit capture (`who/what/when`) | `backend/src/main/java/com/hackathon/audit/auditing/AuditAspect.java`, `backend/src/main/java/com/hackathon/audit/auditing/Auditable.java` |
| Search & filters | `backend/src/main/java/com/hackathon/audit/api/AuditLogController.java`, `backend/src/main/java/com/hackathon/audit/auditing/AuditLogSpecs.java` |
| CSV export | `backend/src/main/java/com/hackathon/audit/api/AuditLogController.java` |
| Redaction | `backend/src/main/java/com/hackathon/audit/auditing/RedactionUtil.java` |
| RBAC | `backend/src/main/java/com/hackathon/audit/config/SecurityConfig.java` |
| Real-time updates | `backend/src/main/java/com/hackathon/audit/config/WebSocketConfig.java`, `frontend-dropin/src/app/audit/audit-log.component.ts` |
| Verification tests | `backend/src/test/java/com/hackathon/audit/api/AuditAspectIntegrationTest.java`, `backend/src/test/java/com/hackathon/audit/auditing/AuditLogSpecsJpaTest.java` |

## Practical Navigation Order

1. Read `README.md` for run/demo commands.
2. Read `ARCHITECTURE.md` for system flow.
3. Inspect `auditing/` package for core implementation.
4. Review `api/AuditLogController.java` for search/export behavior.
5. Review tests in `backend/src/test/java/...` for proof points.
