# Hackathon Requirements Mapping

This document maps each requirement to the implemented code paths.

## Coverage Matrix

| Requirement | Implementation | Evidence |
|---|---|---|
| Capture who did what and when | `@Auditable` + `AuditAspect` persist `AuditLog` | `backend/src/main/java/com/hackathon/audit/auditing/Auditable.java`, `backend/src/main/java/com/hackathon/audit/auditing/AuditAspect.java`, `backend/src/main/java/com/hackathon/audit/auditing/AuditLog.java` |
| Searchable audit logs | Dynamic JPA specs on audit endpoint | `backend/src/main/java/com/hackathon/audit/api/AuditLogController.java`, `backend/src/main/java/com/hackathon/audit/auditing/AuditLogSpecs.java`, `backend/src/main/java/com/hackathon/audit/auditing/AuditLogRepository.java` |
| Date filters | Supports `from+to`, `from` only, `to` only | `backend/src/main/java/com/hackathon/audit/auditing/AuditLogSpecs.java`, `backend/src/main/java/com/hackathon/audit/api/AuditLogController.java` |
| Sensitive data redaction | Masks sensitive keys before storing details | `backend/src/main/java/com/hackathon/audit/auditing/RedactionUtil.java`, usage in `backend/src/main/java/com/hackathon/audit/auditing/AuditAspect.java` |
| Review dashboard | Built-in static demo UI + Angular drop-in module | `backend/src/main/resources/static`, `frontend-dropin/src/app/audit` |
| CSV export | Export endpoint with current filter set | `GET /api/audit-logs/export`, implemented in `backend/src/main/java/com/hackathon/audit/api/AuditLogController.java` |
| Role-based access control | Security config + method-level authorization | `backend/src/main/java/com/hackathon/audit/config/SecurityConfig.java`, `@PreAuthorize` in `backend/src/main/java/com/hackathon/audit/api/AuditLogController.java` |
| Bonus: Real-time updates | STOMP topic push + frontend subscription | `backend/src/main/java/com/hackathon/audit/config/WebSocketConfig.java`, `frontend-dropin/src/app/audit/audit-log.component.ts` |

## Captured Audit Fields

- `username`
- `action`
- `timestamp`
- `entityType`
- `entityId`
- `details` (redacted)
- `userIp`
- `userAgent`

## Verification Tests

- `backend/src/test/java/com/hackathon/audit/api/AuditAspectIntegrationTest.java`
- `backend/src/test/java/com/hackathon/audit/auditing/AuditLogSpecsJpaTest.java`
- `backend/src/test/java/com/hackathon/audit/auditing/RedactionUtilTest.java`
