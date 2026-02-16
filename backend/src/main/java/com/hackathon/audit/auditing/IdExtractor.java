package com.hackathon.audit.auditing;

/**
 * Service Provider Interface for extracting entity IDs from audit contexts.
 * Implementations can provide custom strategies for ID extraction.
 */
public interface IdExtractor {
  /**
   * Extract an entity ID from the given context.
   * @param auditable The @Auditable annotation metadata
   * @param args Method arguments
   * @param result Method return value
   * @return The extracted entity ID, or null if not found
   */
  String extract(Auditable auditable, Object[] args, Object result);
  
  /**
   * Priority of this extractor. Lower values are tried first.
   * @return priority value (default 100)
   */
  default int getPriority() {
    return 100;
  }
}
