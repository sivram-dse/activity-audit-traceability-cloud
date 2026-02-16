package com.hackathon.audit.auditing;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface Auditable {
  String action();
  String entityType() default "";
  String entityIdParam() default "";
}
