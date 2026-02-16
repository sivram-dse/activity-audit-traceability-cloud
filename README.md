# Activity Auditing and Traceability

Spring Boot + Angular-ready audit framework that captures who did what and when, with search, redaction, CSV export, RBAC, and real-time updates.

## Quick Start

### Option 1: Docker Compose
```bash
docker compose up --build
```
- Backend API: `http://localhost:8080`
- Demo UI container: `http://localhost:8088`

Windows note:
- Docker Desktop requires WSL2. If Docker fails to start, run `wsl --install` in an elevated terminal and reboot.

### Option 2: Backend only (Maven)
```bash
cd backend
mvn spring-boot:run
```
- Backend (with built-in static demo page): `http://localhost:8080`

### Run tests
```bash
cd backend
mvn test
```

## Demo Credentials (HTTP Basic)

| Username | Password | Role |
| --- | --- | --- |
| `admin` | `admin123` | `ROLE_ADMIN` |
| `auditor` | `auditor123` | `ROLE_AUDITOR` |
| `user` | `user123` | `ROLE_USER` |

## Core Features

- Automatic audit capture with custom `@Auditable` annotation and AOP
- Searchable logs with dynamic filters (`user`, `action`, `from`, `to`)
- Date filtering supports:
  - `from` + `to`
  - `from` only
  - `to` only
- Sensitive field redaction before persistence
- CSV export of filtered audit logs
- Role-based authorization (ADMIN/AUDITOR for read/export)
- Real-time stream of new audit events via STOMP + SockJS

## API Endpoints

### Audit APIs (ADMIN or AUDITOR)
- `GET /api/audit-logs`
  - Query params: `user`, `action`, `from`, `to`
- `GET /api/audit-logs/export`
  - Same filters, CSV output

### Demo business APIs
- `POST /api/demo/orders` (audited as `CREATE_ORDER`)
- `PUT /api/demo/orders/{id}/status` (audited as `UPDATE_ORDER_STATUS`)
- `DELETE /api/demo/orders/{id}` (audited as `DELETE_ORDER`)

### Demo admin APIs (ADMIN only)
- `POST /api/demo/admin/reset`
- `POST /api/demo/admin/generate?orders=5&eventsPerOrder=4`
- Optional lightweight protection: `DEMO_TOKEN` (`X-Demo-Token` header)

## Architecture Summary

- Annotation: `backend/src/main/java/com/hackathon/audit/auditing/Auditable.java`
- Aspect: `backend/src/main/java/com/hackathon/audit/auditing/AuditAspect.java`
- Model: `backend/src/main/java/com/hackathon/audit/auditing/AuditLog.java`
- Search specs: `backend/src/main/java/com/hackathon/audit/auditing/AuditLogSpecs.java`
- Controller: `backend/src/main/java/com/hackathon/audit/api/AuditLogController.java`
- Security: `backend/src/main/java/com/hackathon/audit/config/SecurityConfig.java`
- WebSocket config: `backend/src/main/java/com/hackathon/audit/config/WebSocketConfig.java`

## Frontend Integration (Angular drop-in)

Drop-in path:
- `frontend-dropin/src/app/audit`

Install dependencies in your Angular app:
```bash
npm install @stomp/rx-stomp sockjs-client
npm install --save-dev @types/sockjs-client
```

Add route:
```ts
{ path: 'admin/audit-logs', component: AuditLogComponent }
```

The drop-in uses SockJS with same-origin endpoint `/ws-audit`, so it works across local, Docker, and Cloud Run deployments without hardcoded host changes.

## Cloud Run

See:
- `CLOUD_RUN.md` for deployment steps
- `HACKATHON_REQUIREMENTS.md` for requirement mapping
- `ARCHITECTURE.md` for architecture diagrams

## Additional Docs

- `DEMO.md`
- `PROJECT_STRUCTURE.md`
- `IMPLEMENTATION_SUMMARY.md`
- `NEW_FEATURES_GUIDE.md`

