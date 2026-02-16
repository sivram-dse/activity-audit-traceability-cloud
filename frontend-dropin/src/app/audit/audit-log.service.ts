import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuditLog } from './audit-log.model';

@Injectable({ providedIn: 'root' })
export class AuditLogService {
  private readonly API_BASE = '/api';

  constructor(private http: HttpClient) {}

  getLogs(filters: { user?: string; action?: string; from?: string; to?: string; }): Observable<AuditLog[]> {
    let params = new HttpParams();
    if (filters.user) params = params.set('user', filters.user);
    if (filters.action) params = params.set('action', filters.action);
    if (filters.from) params = params.set('from', filters.from);
    if (filters.to) params = params.set('to', filters.to);
    return this.http.get<AuditLog[]>(`${this.API_BASE}/audit-logs`, { params });
  }

  exportCsv(filters: { user?: string; action?: string; from?: string; to?: string; }): Observable<Blob> {
    let params = new HttpParams();
    if (filters.user) params = params.set('user', filters.user);
    if (filters.action) params = params.set('action', filters.action);
    if (filters.from) params = params.set('from', filters.from);
    if (filters.to) params = params.set('to', filters.to);
    return this.http.get(`${this.API_BASE}/audit-logs/export`, { params, responseType: 'blob' });
  }
}
