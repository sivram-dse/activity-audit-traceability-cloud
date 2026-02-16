package com.hackathon.audit.demo;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Order {
  private String id;
  private String customerName;
  private String product;
  private String status;
}
