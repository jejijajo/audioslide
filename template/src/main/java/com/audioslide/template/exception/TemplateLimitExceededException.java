package com.audioslide.template.exception;

public class TemplateLimitExceededException extends RuntimeException {
  public TemplateLimitExceededException(String message) {
    super(message);
  }
}
