package com.hackathon.audit.auditing;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AuditLogSpecsJpaTest {

  @Autowired
  AuditLogRepository repo;

  @Test
  void filtersByUserActionAndDateRange() {
    repo.save(AuditLog.builder()
      .username("admin").action("CREATE_ORDER").entityType("Order").entityId("1")
      .details("{\"password\":\"x\"}").timestamp(LocalDateTime.now().minusDays(2))
      .build());

    repo.save(AuditLog.builder()
      .username("auditor").action("UPDATE_ORDER_STATUS").entityType("Order").entityId("2")
      .details("{\"token\":\"y\"}").timestamp(LocalDateTime.now().minusDays(1))
      .build());

    repo.save(AuditLog.builder()
      .username("admin").action("DELETE_ORDER").entityType("Order").entityId("3")
      .details("ok").timestamp(LocalDateTime.now())
      .build());

    LocalDate from = LocalDate.now().minusDays(1);
    LocalDate to = LocalDate.now();

    Specification<AuditLog> spec = Specification.where(AuditLogSpecs.userEquals("admin"))
      .and(AuditLogSpecs.timestampBetween(from, to));

    List<AuditLog> found = repo.findAll(spec, Sort.by(Sort.Direction.ASC, "timestamp"));
    assertThat(found).hasSize(1);
    assertThat(found.get(0).getAction()).isEqualTo("DELETE_ORDER");
  }

  @Test
  void supportsFromOnlyAndToOnlyDateFilters() {
    repo.save(AuditLog.builder()
      .username("admin").action("CREATE_ORDER").entityType("Order").entityId("a1")
      .details("old").timestamp(LocalDateTime.now().minusDays(3))
      .build());

    repo.save(AuditLog.builder()
      .username("admin").action("UPDATE_ORDER_STATUS").entityType("Order").entityId("a2")
      .details("mid").timestamp(LocalDateTime.now().minusDays(1))
      .build());

    repo.save(AuditLog.builder()
      .username("admin").action("DELETE_ORDER").entityType("Order").entityId("a3")
      .details("new").timestamp(LocalDateTime.now())
      .build());

    List<AuditLog> fromOnly = repo.findAll(
      Specification.where(AuditLogSpecs.timestampFrom(LocalDate.now().minusDays(1))),
      Sort.by(Sort.Direction.ASC, "timestamp")
    );
    assertThat(fromOnly).hasSize(2);

    List<AuditLog> toOnly = repo.findAll(
      Specification.where(AuditLogSpecs.timestampTo(LocalDate.now().minusDays(1))),
      Sort.by(Sort.Direction.ASC, "timestamp")
    );
    assertThat(toOnly).hasSize(2);
  }
}
