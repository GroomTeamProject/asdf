package io.goorm.team02.common.exception.errors;

import org.springframework.http.HttpStatus;
import lombok.Getter;

@Getter
public class ConflictException extends RuntimeException {
  public static final String DEFAULT_MESSAGE = "요청에 대한 충돌이 발생했습니다.";
  public static final HttpStatus STATUS = HttpStatus.CONFLICT;

  public ConflictException() {
    super(DEFAULT_MESSAGE);
  }

  public ConflictException(String message) {
    super(message);
  }

  public ConflictException(String message, Throwable cause) {
    super(message, cause);
  }

  public ConflictException(Throwable cause) {
    super(cause);
  }

}
