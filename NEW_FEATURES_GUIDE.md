# New Features Guide

## Overview

This guide highlights the major implementation upgrades that make the project demo-ready:
- Real-time audit monitoring (WebSocket/STOMP + SockJS)
- Angular Signals + Reactive Forms
- SPI-style ID extraction
- Stream-based backend query/export flow
- Improved date filter behavior (`from`/`to` independent support)

## 1) Real-Time Audit Monitoring

### Backend
- `WebSocketConfig` exposes SockJS endpoint: `/ws-audit`
- Audit events are published to topic: `/topic/audit-logs`
- `AuditAspect` broadcasts each saved audit record

### Frontend
- Angular drop-in uses `@stomp/rx-stomp` + `sockjs-client`
- Subscribes to `/topic/audit-logs`
- New events are prepended in UI state immediately

### Why this matters
- Judges can trigger an action and watch the audit row appear instantly
- No manual page refresh required

## 2) Angular Signals + Reactive Forms

### Signals used for UI state
- `logs`
- `loading`
- `error`

### Reactive form controls
- `user`
- `action`
- `from`
- `to`

### Why this matters
- Cleaner state updates
- Better maintainability and typing
- Consistent filter handling and export requests

## 3) Flexible Date Filtering

Search endpoint now supports:
- `from` + `to` (range)
- `from` only
- `to` only

Implemented in:
- `backend/src/main/java/com/hackathon/audit/auditing/AuditLogSpecs.java`
- `backend/src/main/java/com/hackathon/audit/api/AuditLogController.java`

## 4) SPI-style Entity ID Extraction

Pattern:
- `IdExtractor` interface defines extraction contract + priority
- `EntityIdExtractor` delegates over all registered extractors
- `ReflectionIdExtractor` acts as fallback

Why this matters:
- Add custom ID strategies without changing core aspect logic
- Supports growth of audited domain models

## 5) Stream-based Backend Processing

Implemented improvements:
- Specification composition via Stream pipeline
- CSV export row generation via Stream mapping

Why this matters:
- Clear functional flow
- Easier to extend filtering and transformation logic

## 6) Validation and Test Coverage

Key tests:
- `AuditAspectIntegrationTest` (captures and masks sensitive data)
- `AuditLogSpecsJpaTest` (date/user/action filter behavior)
- `RedactionUtilTest` (masking rules)

Run:
```bash
cd backend
mvn test
```

## 7) Quick Verification Script (Manual)

1. Start backend:
```bash
cd backend
mvn spring-boot:run
```

2. Trigger audited action:
```bash
curl -u user:user123 -X POST http://localhost:8080/api/demo/orders \
  -H "Content-Type: application/json" \
  -d '{"customerName":"Alice","product":"Widget","password":"secret123"}'
```

3. Query logs:
```bash
curl -u admin:admin123 "http://localhost:8080/api/audit-logs?action=CREATE_ORDER"
```

4. Export CSV:
```bash
curl -u auditor:auditor123 "http://localhost:8080/api/audit-logs/export?action=CREATE_ORDER"
```

## 8) Frontend Dependency Notes

Install in Angular app:
```bash
npm install @stomp/rx-stomp sockjs-client
npm install --save-dev @types/sockjs-client
```

The drop-in component uses same-origin SockJS endpoint (`/ws-audit`) so it works across local, Docker, and Cloud Run environments.

