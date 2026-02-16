# Demo Guide (2-5 Minutes)

This walkthrough is designed for fast hackathon judging.

## 1) Start the System

### Option A: Docker Compose
```bash
docker compose up --build
```
- Backend: `http://localhost:8080`
- Demo UI container: `http://localhost:8088`

### Option B: Backend only
```bash
cd backend
mvn spring-boot:run
```
- Backend + built-in static UI: `http://localhost:8080`

## 2) Demo Credentials

- `admin / admin123` (view/export/admin demo controls)
- `auditor / auditor123` (view/export)
- `user / user123` (perform auditable actions)

## 3) Trigger Auditable Actions

Create:
```bash
curl -u user:user123 -X POST "http://localhost:8080/api/demo/orders" \
  -H "Content-Type: application/json" \
  -d '{"customerName":"Siv Ram","product":"Laptop Stand","password":"secret123"}'
```

Update status:
```bash
curl -u user:user123 -X PUT "http://localhost:8080/api/demo/orders/<ORDER_ID>/status?status=APPROVED"
```

Delete:
```bash
curl -u user:user123 -X DELETE "http://localhost:8080/api/demo/orders/<ORDER_ID>"
```

## 4) Show Search and Export

List logs:
```bash
curl -u admin:admin123 "http://localhost:8080/api/audit-logs"
```

Filter by user + action:
```bash
curl -u admin:admin123 "http://localhost:8080/api/audit-logs?user=user&action=CREATE_ORDER"
```

Filter by date (examples):
```bash
curl -u admin:admin123 "http://localhost:8080/api/audit-logs?from=2026-02-01"
curl -u admin:admin123 "http://localhost:8080/api/audit-logs?to=2026-02-16"
```

Export CSV:
```bash
curl -u auditor:auditor123 -L "http://localhost:8080/api/audit-logs/export?action=CREATE_ORDER" -o audit_logs.csv
```

## 5) Optional Real-Time Demo

If using the Angular drop-in or built-in UI with live updates:
1. Open dashboard page.
2. Trigger a `POST /api/demo/orders`.
3. Show new audit entry appearing without page refresh.

## 6) Optional Demo Controls (Admin)

Reset and reseed:
```bash
curl -u admin:admin123 -X POST "http://localhost:8080/api/demo/admin/reset"
```

Generate synthetic activity:
```bash
curl -u admin:admin123 -X POST "http://localhost:8080/api/demo/admin/generate?orders=5&eventsPerOrder=4"
```

If `DEMO_TOKEN` is configured, include header:
```text
X-Demo-Token: <value>
```

## What to Explicitly Call Out to Judges

- Automatic capture of who/what/when via annotation + AOP
- Sensitive data masking in `details`
- Search by user/action/date (`from` and `to` independently supported)
- CSV export for compliance workflows
- RBAC enforcement (ADMIN/AUDITOR vs USER)
- Real-time monitoring capability

