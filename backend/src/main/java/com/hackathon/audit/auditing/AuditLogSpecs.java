package com.hackathon.audit.auditing;

import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;

public final class AuditLogSpecs {
  private AuditLogSpecs() {}

  public static Specification<AuditLog> userEquals(String username) {
    return (root, q, cb) -> cb.equal(root.get("username"), username);
  }

  public static Specification<AuditLog> actionEquals(String action) {
    return (root, q, cb) -> cb.equal(root.get("action"), action);
  }

  public static Specification<AuditLog> timestampBetween(LocalDate from, LocalDate to) {
    LocalDateTime start = from.atStartOfDay();
    LocalDateTime end = to.atTime(23, 59, 59);
    return (root, q, cb) -> cb.between(root.get("timestamp"), start, end);
  }

  public static Specification<AuditLog> timestampFrom(LocalDate from) {
    LocalDateTime start = from.atStartOfDay();
    return (root, q, cb) -> cb.greaterThanOrEqualTo(root.get("timestamp"), start);
  }

  public static Specification<AuditLog> timestampTo(LocalDate to) {
    LocalDateTime end = to.atTime(23, 59, 59);
    return (root, q, cb) -> cb.lessThanOrEqualTo(root.get("timestamp"), end);
  }
}
