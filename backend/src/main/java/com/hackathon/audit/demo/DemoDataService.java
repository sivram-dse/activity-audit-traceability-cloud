package com.hackathon.audit.demo;

import com.hackathon.audit.auditing.AuditLog;
import com.hackathon.audit.auditing.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Utilities to make demos reliable on ephemeral environments (like Cloud Run + H2).
 * Provides reset/reseed and synthetic event generation.
 */
@Service
@RequiredArgsConstructor
public class DemoDataService {

  private final AuditLogRepository auditRepo;
  private final OrderStore orderStore;

  public record ResetResult(long deletedLogs, int clearedOrders, long seededLogs) {}
  public record GenerateResult(long createdLogs, int createdOrders) {}

  public ResetResult resetAndSeed() {
    long deleted = auditRepo.count();
    auditRepo.deleteAllInBatch();
    int clearedOrders = orderStore.map().size();
    orderStore.clear();

    long seeded = seedBaselineLogs();
    return new ResetResult(deleted, clearedOrders, seeded);
  }

  public long seedIfEmpty() {
    if (auditRepo.count() > 0) return 0;
    return seedBaselineLogs();
  }

  private long seedBaselineLogs() {
    List<AuditLog> seed = List.of(
      AuditLog.builder()
        .username("system")
        .action("BOOTSTRAP")
        .entityType("AuditLog")
        .entityId("N/A")
        .details("{\"message\":\"Sample audit logs inserted for demo\"}")
        .timestamp(LocalDateTime.now().minusMinutes(5))
        .userIp("127.0.0.1")
        .userAgent("DemoDataService")
        .build(),
      AuditLog.builder()
        .username("user")
        .action("CREATE_ORDER")
        .entityType("Order")
        .entityId("1")
        .details("{\"customerName\":\"Siv Ram\",\"product\":\"Laptop Stand\",\"status\":\"CREATED\"}")
        .timestamp(LocalDateTime.now().minusMinutes(4))
        .userIp("127.0.0.1")
        .userAgent("DemoDataService")
        .build(),
      AuditLog.builder()
        .username("admin")
        .action("REDACTION_DEMO")
        .entityType("User")
        .entityId("u-42")
        .details("{\"email\":\"sivram@example.com\",\"phone\":\"+91-9876543210\",\"note\":\"Should be redacted if configured\"}")
        .timestamp(LocalDateTime.now().minusMinutes(3))
        .userIp("127.0.0.1")
        .userAgent("DemoDataService")
        .build()
    );
    auditRepo.saveAll(seed);
    return seed.size();
  }

  public GenerateResult generateSyntheticEvents(int orders, int eventsPerOrder) {
    int safeOrders = Math.max(0, Math.min(orders, 50));
    int safeEvents = Math.max(1, Math.min(eventsPerOrder, 20));

    Random rnd = new Random();
    long createdLogs = 0;
    int createdOrders = 0;

    String[] products = {"Laptop Stand", "Headphones", "Mechanical Keyboard", "USB Hub", "Monitor Arm", "Webcam"};
    String[] names = {"Siv Ram", "Ananya", "Vikram", "Meera", "Arjun", "Priya"};
    String[] statuses = {"CREATED", "PAID", "PACKED", "SHIPPED", "DELIVERED", "CANCELLED"};

    for (int i = 0; i < safeOrders; i++) {
      String id = UUID.randomUUID().toString();
      Order o = Order.builder()
        .id(id)
        .customerName(names[rnd.nextInt(names.length)])
        .product(products[rnd.nextInt(products.length)])
        .status("CREATED")
        .build();
      orderStore.map().put(id, o);
      createdOrders++;

      createdLogs += saveLog("user", "CREATE_ORDER", "Order", id,
        "{\"customerName\":\"" + o.getCustomerName() + "\",\"product\":\"" + o.getProduct() + "\",\"status\":\"CREATED\"}");

      for (int e = 0; e < safeEvents; e++) {
        String st = statuses[rnd.nextInt(statuses.length)];
        o.setStatus(st);
        createdLogs += saveLog("user", "UPDATE_ORDER_STATUS", "Order", id,
          "{\"status\":\"" + st + "\",\"note\":\"Auto-generated demo event\"}");
      }

      if (rnd.nextDouble() < 0.2) {
        orderStore.map().remove(id);
        createdLogs += saveLog("admin", "DELETE_ORDER", "Order", id,
          "{\"reason\":\"Auto-cleanup demo event\"}");
      }
    }

    return new GenerateResult(createdLogs, createdOrders);
  }

  private long saveLog(String username, String action, String entityType, String entityId, String detailsJson) {
    auditRepo.save(AuditLog.builder()
      .username(username)
      .action(action)
      .entityType(entityType)
      .entityId(entityId)
      .details(detailsJson)
      .timestamp(LocalDateTime.now())
      .userIp("0.0.0.0")
      .userAgent("DemoGenerator")
      .build());
    return 1;
  }
}
