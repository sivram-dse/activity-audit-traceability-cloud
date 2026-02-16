package com.hackathon.audit.auditing;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

  private final AuditLogRepository repo;
  private final SimpMessagingTemplate messagingTemplate;
  private final EntityIdExtractor entityIdExtractor;
  private final ObjectMapper mapper = new ObjectMapper();

  @AfterReturning(value = "@annotation(auditable)", returning = "result")
  public void afterSuccess(JoinPoint jp, Auditable auditable, Object result) {
    String username = currentUsername();
    HttpServletRequest req = currentRequest();

    String entityType = auditable.entityType().isBlank()
      ? jp.getTarget().getClass().getSimpleName()
      : auditable.entityType();

    String entityId = entityIdExtractor.extract(auditable, jp.getArgs(), result);

    String details = buildDetails(jp.getArgs(), result);

    AuditLog log = AuditLog.builder()
      .username(username)
      .action(auditable.action())
      .entityType(entityType)
      .entityId(entityId)
      .details(details)
      .timestamp(LocalDateTime.now())
      .userIp(req != null ? req.getRemoteAddr() : null)
      .userAgent(req != null ? req.getHeader("User-Agent") : null)
      .build();

    repo.save(log);
    
    // Broadcast audit log via WebSocket for real-time updates
    messagingTemplate.convertAndSend("/topic/audit-logs", log);
  }

  private String buildDetails(Object[] args, Object result) {
    try {
      String argsJson = mapper.writeValueAsString(args);
      String resultJson = mapper.writeValueAsString(result);
      String combined = "{\"args\":" + argsJson + ",\"result\":" + resultJson + "}";
      return RedactionUtil.maskJsonLike(combined);
    } catch (Exception e) {
      return "args=" + RedactionUtil.maskJsonLike(String.valueOf(args)) +
        ", result=" + RedactionUtil.maskJsonLike(String.valueOf(result));
    }
  }

  private String currentUsername() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || auth.getName() == null) return "anonymous";
    return auth.getName();
  }

  private HttpServletRequest currentRequest() {
    try {
      ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
      return attrs.getRequest();
    } catch (Exception e) {
      return null;
    }
  }
}
