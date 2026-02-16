package com.hackathon.audit.demo;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Ensures the demo UI is not empty on first run.
 * Inserts a few sample audit logs only if the table is empty.
 */
@Component
@RequiredArgsConstructor
public class SeedDataRunner implements CommandLineRunner {

  private final DemoDataService demoDataService;

  @Override
  public void run(String... args) {
    demoDataService.seedIfEmpty();
  }
}
