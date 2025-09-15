package io.goorm.team02.core.common.exception.dto;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class ErrorResponse {
  private LocalDateTime timestamp;
  private int status;
  private String error;
  private String message;

  public ErrorResponse(LocalDateTime timestamp, int status, String error, String message) {
    this.timestamp = timestamp;
    this.status = status;
    this.error = error;
    this.message = message;
  }

  public static ErrorResponse of(HttpStatus status, String message) {
    return new ErrorResponse(LocalDateTime.now(), status.value(), status.getReasonPhrase(), message);
  }
}
