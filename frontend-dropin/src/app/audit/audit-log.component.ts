import { Component, OnInit, OnDestroy, signal } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { RxStomp } from '@stomp/rx-stomp';
import { Subscription } from 'rxjs';
import SockJS from 'sockjs-client';
import { AuditLogService } from './audit-log.service';
import { AuditLog } from './audit-log.model';

@Component({
  selector: 'app-audit-log',
  templateUrl: './audit-log.component.html',
  styleUrls: ['./audit-log.component.scss']
})
export class AuditLogComponent implements OnInit, OnDestroy {
  // Angular Signals for reactive state management
  logs = signal<AuditLog[]>([]);
  loading = signal(false);
  error = signal('');
  
  // Reactive Form
  filterForm: FormGroup;
  
  // WebSocket
  private rxStomp = new RxStomp();
  private wsSubscription?: Subscription;

  constructor(
    private audit: AuditLogService,
    private fb: FormBuilder
  ) {
    // Initialize reactive form
    this.filterForm = this.fb.group({
      user: [''],
      action: [''],
      from: [''],
      to: ['']
    });
  }

  ngOnInit(): void {
    this.connectWebSocket();
  }

  ngOnDestroy(): void {
    this.wsSubscription?.unsubscribe();
    this.rxStomp.deactivate();
  }

  private connectWebSocket(): void {
    const sockJsEndpoint = `${window.location.origin}/ws-audit`;

    this.rxStomp.configure({
      webSocketFactory: () => new SockJS(sockJsEndpoint),
      reconnectDelay: 5000,
    });

    this.rxStomp.activate();

    // Subscribe to real-time audit log updates
    this.wsSubscription = this.rxStomp.watch('/topic/audit-logs').subscribe((message) => {
      const newLog: AuditLog = JSON.parse(message.body);
      // Prepend new log to existing logs using signal update
      this.logs.update(logs => [newLog, ...logs]);
    });
  }

  search(): void {
    this.loading.set(true);
    this.error.set('');
    
    const filters = this.filterForm.value;
    
    this.audit.getLogs({
      user: filters.user || undefined,
      action: filters.action || undefined,
      from: filters.from || undefined,
      to: filters.to || undefined,
    }).subscribe({
      next: (data) => {
        this.logs.set(data);
        this.loading.set(false);
      },
      error: (e) => {
        this.error.set('Failed to load logs. Check auth/URL/CORS.');
        this.loading.set(false);
      }
    });
  }

  clear(): void {
    this.filterForm.reset();
    this.logs.set([]);
    this.error.set('');
  }

  exportCsv(): void {
    const filters = this.filterForm.value;
    
    this.audit.exportCsv({
      user: filters.user || undefined,
      action: filters.action || undefined,
      from: filters.from || undefined,
      to: filters.to || undefined,
    }).subscribe({
      next: (blob) => this.download(blob, 'audit_logs.csv'),
      error: () => this.error.set('Export failed. Check auth/URL/CORS.')
    });
  }

  private download(blob: Blob, filename: string) {
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = filename;
    a.click();
    window.URL.revokeObjectURL(url);
  }
}
