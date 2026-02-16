package com.hackathon.audit.auditing;

import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * Default implementation of IdExtractor using reflection.
 * This is the fallback strategy for extracting entity IDs.
 */
@Component
public class ReflectionIdExtractor implements IdExtractor {

  @Override
  public String extract(Auditable auditable, Object[] args, Object result) {
    // 1) If annotation specifies entityIdParam and args is a single primitive/string -> use that.
    if (!auditable.entityIdParam().isBlank() && args != null) {
      for (Object a : args) {
        if (a == null) continue;
        // If arg is simple, return it (typical for path variable id)
        if (a instanceof String || a instanceof Number) return String.valueOf(a);
        // Try getter getId()
        String id = tryGetId(a);
        if (id != null) return id;
      }
    }

    // 2) Try getId() from result
    return tryGetId(result);
  }

  private String tryGetId(Object obj) {
    if (obj == null) return null;
    try {
      Method m = obj.getClass().getMethod("getId");
      Object val = m.invoke(obj);
      return val != null ? String.valueOf(val) : null;
    } catch (Exception ignored) {
      return null;
    }
  }

  @Override
  public int getPriority() {
    return 999; // Low priority - used as fallback
  }
}
