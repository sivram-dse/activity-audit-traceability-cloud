package com.hackathon.audit.demo;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory store for demo orders. For demo/hackathon purposes only.
 */
@Component
public class OrderStore {

  private final Map<String, Order> store = new ConcurrentHashMap<>();

  public Map<String, Order> map() {
    return store;
  }

  public void clear() {
    store.clear();
  }
}
