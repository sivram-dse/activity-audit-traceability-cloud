package com.hackathon.audit.auditing;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RedactionUtilTest {

  @Test
  void masksSensitiveKeysInJsonLikeStrings() {
    String input = "{\"username\":\"siv\",\"password\":\"secret123\",\"token\":\"abc\",\"pan\":\"ABCDE1234F\"}";
    String masked = RedactionUtil.maskJsonLike(input);

    assertThat(masked).doesNotContain("secret123");
    assertThat(masked).doesNotContain("abc");
    assertThat(masked).doesNotContain("ABCDE1234F");
    assertThat(masked).contains("\"password\":\"***\"");
    assertThat(masked).contains("\"token\":\"***\"");
    assertThat(masked).contains("\"pan\":\"***\"");
  }
}
