package com.hackathon.audit.auditing;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_log", indexes = {
  @Index(name = "idx_audit_ts", columnList = "timestamp"),
  @Index(name = "idx_audit_user", columnList = "username"),
  @Index(name = "idx_audit_action", columnList = "action")
})
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AuditLog {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 120)
  private String username;

  @Column(nullable = false, length = 120)
  private String action;

  @Column(length = 120)
  private String entityType;

  @Column(length = 120)
  private String entityId;

  @Column(length = 4000)
  private String details;

  @Column(nullable = false)
  private LocalDateTime timestamp;

  @Column(length = 80)
  private String userIp;

  @Column(length = 200)
  private String userAgent;
}
