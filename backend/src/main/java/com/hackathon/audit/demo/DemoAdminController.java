package com.hackathon.audit.demo;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Demo utilities for hackathon judging.
 * Reset/reseed and generate sample events so the dashboard is always alive.
 */
@RestController
@RequestMapping("/api/demo/admin")
@RequiredArgsConstructor
public class DemoAdminController {

  private final DemoDataService demoDataService;

  /**
   * Optional lightweight protection for public demos.
   * If demo.token is set (or env DEMO_TOKEN), endpoints also require X-Demo-Token header.
   */
  @Value("${demo.token:}")
  private String demoToken;

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/reset")
  public DemoDataService.ResetResult reset(HttpServletRequest request) {
    requireTokenIfConfigured(request);
    return demoDataService.resetAndSeed();
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/generate")
  public DemoDataService.GenerateResult generate(
    @RequestParam(defaultValue = "5") int orders,
    @RequestParam(defaultValue = "4") int eventsPerOrder,
    HttpServletRequest request
  ) {
    requireTokenIfConfigured(request);
    return demoDataService.generateSyntheticEvents(orders, eventsPerOrder);
  }

  private void requireTokenIfConfigured(HttpServletRequest request) {
    if (demoToken == null || demoToken.isBlank()) return;

    String provided = request.getHeader("X-Demo-Token");
    if (provided == null || provided.isBlank()) {
      provided = request.getParameter("demoToken");
    }
    if (!demoToken.equals(provided)) {
      throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED, "Invalid demo token");
    }
  }
}
