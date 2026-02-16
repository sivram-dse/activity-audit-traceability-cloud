export interface AuditLog {
  id: number;
  username: string;
  action: string;
  entityType?: string;
  entityId?: string;
  details?: string;
  timestamp: string;
  userIp?: string;
  userAgent?: string;
}
