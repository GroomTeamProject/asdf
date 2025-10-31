package io.goorm.team02.common.exception.errors;

import org.springframework.http.HttpStatus;
import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException {
  public static final String DEFAULT_MESSAGE = "요청에 대한 항목을 찾을 수 없습니다.";
  public static final HttpStatus STATUS = HttpStatus.NOT_FOUND;

  public NotFoundException() {
    super(DEFAULT_MESSAGE);
  }

  public NotFoundException(String message) {
    super(message);
  }

  public NotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  public NotFoundException(Throwable cause) {
    super(cause);
  }
}
