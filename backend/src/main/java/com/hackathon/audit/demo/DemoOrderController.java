package com.hackathon.audit.demo;

import com.hackathon.audit.auditing.Auditable;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/demo/orders")
public class DemoOrderController {

  private final OrderStore orderStore;

  public DemoOrderController(OrderStore orderStore) {
    this.orderStore = orderStore;
  }

  @Auditable(action = "CREATE_ORDER", entityType = "Order", entityIdParam = "id")
  @PostMapping
  public Order create(@Valid @RequestBody OrderDto dto) {
    String id = UUID.randomUUID().toString();
    Order o = Order.builder()
      .id(id)
      .customerName(dto.getCustomerName())
      .product(dto.getProduct())
      .status("CREATED")
      .build();
    orderStore.map().put(id, o);
    return o;
  }

  @Auditable(action = "UPDATE_ORDER_STATUS", entityType = "Order", entityIdParam = "id")
  @PutMapping("/{id}/status")
  public Order updateStatus(@PathVariable String id, @RequestParam String status) {
    Order o = orderStore.map().get(id);
    if (o == null) throw new IllegalArgumentException("Order not found: " + id);
    o.setStatus(status);
    return o;
  }

  @Auditable(action = "DELETE_ORDER", entityType = "Order", entityIdParam = "id")
  @DeleteMapping("/{id}")
  public void delete(@PathVariable String id) {
    orderStore.map().remove(id);
  }
}
