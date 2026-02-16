# Key Use Cases

This document captures the primary scenarios supported by the project.

## Actors

- `USER`: performs business actions that are audited
- `AUDITOR`: searches and exports audit logs
- `ADMIN`: same as auditor + demo admin controls
- `SYSTEM`: captures and stores audit events automatically

## UC-001: Audited Business Operation

**Goal:** A user performs an operation and the system records it automatically.

Flow:
1. Authenticated `USER` calls an audited endpoint (for example `POST /api/demo/orders`).
2. `@Auditable` + `AuditAspect` intercept the call.
3. System captures user, action, timestamp, entity context, request metadata.
4. Sensitive fields are redacted before writing details.
5. Audit record is persisted and optionally broadcast to WebSocket topic.

Success criteria:
- Business action succeeds
- Audit record exists with who/what/when and redacted details

## UC-002: Search Audit Trail

**Goal:** Admin/auditor retrieves relevant logs quickly.

Endpoint:
- `GET /api/audit-logs`

Supported filters:
- `user`
- `action`
- `from`
- `to`

Date behavior:
- `from` + `to`
- `from` only
- `to` only

Success criteria:
- Only authorized roles can access
- Results match filter criteria

## UC-003: Export Audit Records

**Goal:** Admin/auditor exports logs for compliance/offline review.

Endpoint:
- `GET /api/audit-logs/export`

Behavior:
- Uses same filters as search endpoint
- Returns CSV response suitable for spreadsheets/tools

Success criteria:
- Export respects filters and role checks
- Data is readable and redacted where needed

## UC-004: Real-Time Monitoring

**Goal:** Dashboard reflects new events without refresh.

Flow:
1. Client subscribes to `/topic/audit-logs` via `/ws-audit` (SockJS/STOMP).
2. New audited operations are pushed from backend.
3. UI prepends incoming audit event.

Success criteria:
- New audit events appear live in connected dashboards

## UC-005: Compliance Review for a User or Action

**Goal:** Auditor investigates specific user/action activity over time.

Flow:
1. Auditor applies filters by `user`, `action`, and date.
2. Reviews timeline in UI/API response.
3. Exports CSV for evidence/reporting if needed.

Success criteria:
- Complete and traceable activity history is retrievable

## UC-006: Demo Reliability on Ephemeral Environments

**Goal:** Keep demos usable on transient infrastructure (for example Cloud Run + H2).

Admin endpoints:
- `POST /api/demo/admin/reset`
- `POST /api/demo/admin/generate`

Optional hardening:
- `DEMO_TOKEN` with `X-Demo-Token`

Success criteria:
- Judge can quickly reseed and regenerate activity on demand

## Access Control Summary

| Capability | USER | AUDITOR | ADMIN |
|---|---|---|---|
| Perform auditable business actions | Yes | No (typically) | Yes |
| View/search audit logs | No | Yes | Yes |
| Export audit CSV | No | Yes | Yes |
| Reset/generate demo data | No | No | Yes |

## Traceability to Code

- Annotation and aspect: `backend/src/main/java/com/hackathon/audit/auditing`
- Search/export API: `backend/src/main/java/com/hackathon/audit/api/AuditLogController.java`
- Security rules: `backend/src/main/java/com/hackathon/audit/config/SecurityConfig.java`
- Real-time config: `backend/src/main/java/com/hackathon/audit/config/WebSocketConfig.java`
- Angular drop-in UI: `frontend-dropin/src/app/audit`
