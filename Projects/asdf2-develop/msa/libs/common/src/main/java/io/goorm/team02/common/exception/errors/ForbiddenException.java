package io.goorm.team02.common.exception.errors;

import org.springframework.http.HttpStatus;
import lombok.Getter;

@Getter
public class ForbiddenException extends RuntimeException {
  public static final String DEFAULT_MESSAGE = "요청에 대한 권한이 충분하지 않습니다.";
  public static final HttpStatus STATUS = HttpStatus.FORBIDDEN;

  public ForbiddenException() {
    super(DEFAULT_MESSAGE);
  }

  public ForbiddenException(String message) {
    super(message);
  }

  public ForbiddenException(String message, Throwable cause) {
    super(message, cause);
  }

  public ForbiddenException(Throwable cause) {
    super(cause);
  }
}
