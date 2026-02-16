package com.hackathon.audit.auditing;

import java.util.Set;
import java.util.regex.Pattern;

public final class RedactionUtil {
  private RedactionUtil() {}

  private static final Set<String> SENSITIVE_KEYS = Set.of(
    "password","passwd","secret","token","accessToken","refreshToken",
    "authorization","apiKey","apikey","ssn","pan","aadhar","creditCard"
  );

  public static String maskJsonLike(String s) {
    if (s == null) return null;
    String masked = s;
    for (String key : SENSITIVE_KEYS) {
      masked = masked.replaceAll("(\"" + Pattern.quote(key) + "\"\\s*:\\s*)\".*?\"", "$1\"***\"");
      masked = masked.replaceAll("(\"" + Pattern.quote(key) + "\"\\s*:\\s*)([^\",}\\]]+)", "$1\"***\"");
    }
    return masked;
  }
}
