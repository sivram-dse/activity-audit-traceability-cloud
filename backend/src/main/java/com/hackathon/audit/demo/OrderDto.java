package com.hackathon.audit.demo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OrderDto {
  @NotBlank
  private String customerName;

  @NotBlank
  private String product;

  // Example sensitive-ish field to show redaction:
  private String token;

  // Included to demonstrate masking of common sensitive fields in audit payloads.
  private String password;
}
