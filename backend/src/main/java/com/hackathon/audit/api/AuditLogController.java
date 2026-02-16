package com.hackathon.audit.api;

import com.hackathon.audit.auditing.AuditLog;
import com.hackathon.audit.auditing.AuditLogRepository;
import com.hackathon.audit.auditing.AuditLogSpecs;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/audit-logs")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN') or hasRole('AUDITOR')")
public class AuditLogController {

  private final AuditLogRepository repo;

  @GetMapping
  public List<AuditLog> list(
    @RequestParam(required = false) String user,
    @RequestParam(required = false) String action,
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
  ) {
    // Use Stream API to compose Specifications dynamically
    Specification<AuditLog> spec = java.util.stream.Stream.of(
      user != null && !user.isBlank() ? AuditLogSpecs.userEquals(user) : null,
      action != null && !action.isBlank() ? AuditLogSpecs.actionEquals(action) : null,
      from != null && to != null ? AuditLogSpecs.timestampBetween(from, to) : null,
      from != null && to == null ? AuditLogSpecs.timestampFrom(from) : null,
      from == null && to != null ? AuditLogSpecs.timestampTo(to) : null
    )
    .filter(java.util.Objects::nonNull)
    .reduce(Specification.where(null), Specification::and);

    return repo.findAll(spec, Sort.by(Sort.Direction.DESC, "timestamp"));
  }

  @GetMapping(value = "/export", produces = "text/csv")
  public void exportCsv(
    HttpServletResponse response,
    @RequestParam(required = false) String user,
    @RequestParam(required = false) String action,
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
  ) throws IOException {

    response.setHeader("Content-Disposition", "attachment; filename=audit_logs.csv");
    List<AuditLog> logs = list(user, action, from, to);

    try (PrintWriter w = response.getWriter()) {
      w.println("Timestamp,User,Action,EntityType,EntityId,UserIp,Details");
      DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

      // Use Stream API to transform and format data
      logs.stream()
        .map(log -> String.format("%s,%s,%s,%s,%s,%s,%s",
          csv(fmt.format(log.getTimestamp())),
          csv(log.getUsername()),
          csv(log.getAction()),
          csv(log.getEntityType()),
          csv(log.getEntityId()),
          csv(log.getUserIp()),
          csv(log.getDetails())
        ))
        .forEach(w::println);
      
      w.flush();
    }
  }

  private String csv(String v) {
    if (v == null) return "";
    String s = v.replace("\r", " ").replace("\n", " ");
    // Quote if contains comma or quotes
    if (s.contains(",") || s.contains("\"")) {
      s = s.replace("\"", "\"\"");
      return "\"" + s + "\"";
    }
    return s;
  }
}
