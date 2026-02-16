package com.hackathon.audit.auditing;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

/**
 * Composite ID extractor that uses SPI pattern to delegate to registered extractors.
 * Falls back to reflection-based extraction if no custom extractors succeed.
 */
@Component
@RequiredArgsConstructor
public class EntityIdExtractor {
  
  private final List<IdExtractor> extractors;
  
  public String extract(Auditable auditable, Object[] args, Object result) {
    // Try all registered extractors in priority order
    String id = extractors.stream()
      .sorted(Comparator.comparingInt(IdExtractor::getPriority))
      .map(extractor -> extractor.extract(auditable, args, result))
      .filter(value -> value != null && !value.isBlank())
      .findFirst()
      .orElse(null);
    
    return id;
  }
}
