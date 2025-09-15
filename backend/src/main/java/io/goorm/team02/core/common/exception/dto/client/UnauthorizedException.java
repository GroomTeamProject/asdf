package io.goorm.team02.core.common.exception.dto.client;

import org.springframework.http.HttpStatus;
import lombok.Getter;

@Getter
public class UnauthorizedException extends RuntimeException {
  public static final String DEFAULT_MESSAGE = "인증이 필요한 요청입니다.";
  public static final HttpStatus STATUS = HttpStatus.UNAUTHORIZED;

  public UnauthorizedException() {
    super(DEFAULT_MESSAGE);
  }

  public UnauthorizedException(String message) {
    super(message);
  }

  public UnauthorizedException(String message, Throwable cause) {
    super(message, cause);
  }

  public UnauthorizedException(Throwable cause) {
    super(cause);
  }
}
