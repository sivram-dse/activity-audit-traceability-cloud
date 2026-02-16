package com.hackathon.audit.api;

import com.hackathon.audit.auditing.AuditLog;
import com.hackathon.audit.auditing.AuditLogRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
  "spring.datasource.url=jdbc:h2:mem:audit-integration;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
  "spring.datasource.driverClassName=org.h2.Driver",
  "spring.datasource.username=sa",
  "spring.datasource.password="
})
class AuditAspectIntegrationTest {

  @Autowired
  MockMvc mvc;

  @Autowired
  AuditLogRepository repo;

  @Test
  void createsAuditLogOnAnnotatedEndpoint() throws Exception {
    long before = repo.count();

    mvc.perform(post("/api/demo/orders")
        .with(httpBasic("user", "user123"))
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"customerName\":\"Siv\",\"product\":\"Laptop\",\"password\":\"topsecret\"}"))
      .andExpect(status().isOk());

    assertThat(repo.count()).isEqualTo(before + 1);

    AuditLog latest = repo.findAll().stream()
      .sorted((a,b) -> b.getTimestamp().compareTo(a.getTimestamp()))
      .findFirst().orElseThrow();

    assertThat(latest.getAction()).isEqualTo("CREATE_ORDER");
    assertThat(latest.getUsername()).isEqualTo("user");
    // Ensure masking happened
    assertThat(latest.getDetails()).contains("\"password\":\"***\"");
    assertThat(latest.getDetails()).doesNotContain("topsecret");
  }
}
