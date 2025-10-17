package io.goorm.team02.core.common.exception;

import io.goorm.team02.core.common.exception.dto.ErrorResponse;
import io.goorm.team02.core.common.exception.dto.client.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("GlobalExceptionHandler 단위 테스트")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("IllegalArgumentException을 400 Bad Request로 처리한다")
    void handleIllegalArgumentException() {
        // given
        String expectedMessage = "잘못된 인수입니다";
        IllegalArgumentException exception = new IllegalArgumentException(expectedMessage);

        // when
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleBadRequestException(exception);

        // then
        assertHttpStatus(response, HttpStatus.BAD_REQUEST, expectedMessage);
    }

    @Test
    @DisplayName("IllegalStateException을 400 Bad Request로 처리한다")
    void handleIllegalStateException() {
        // given
        String expectedMessage = "잘못된 상태입니다";
        IllegalStateException exception = new IllegalStateException(expectedMessage);

        // when
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleBadRequestException(exception);

        // then
        assertHttpStatus(response, HttpStatus.BAD_REQUEST, expectedMessage);
    }

    @Test
    @DisplayName("UnauthorizedException을 401 Unauthorized로 처리한다")
    void handleUnauthorizedException() {
        // given
        String expectedMessage = "인증이 필요합니다";
        UnauthorizedException exception = new UnauthorizedException(expectedMessage);

        // when
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleUnauthorizedException(exception);

        // then
        assertHttpStatus(response, HttpStatus.UNAUTHORIZED, expectedMessage);
    }

    @Test
    @DisplayName("ForbiddenException을 403 Forbidden으로 처리한다")
    void handleForbiddenException() {
        // given
        String expectedMessage = "권한이 없습니다";
        ForbiddenException exception = new ForbiddenException(expectedMessage);

        // when
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleForbiddenException(exception);

        // then
        assertHttpStatus(response, HttpStatus.FORBIDDEN, expectedMessage);
    }

    @Test
    @DisplayName("NotFoundException을 404 Not Found로 처리한다")
    void handleNotFoundException() {
        // given
        String expectedMessage = "리소스를 찾을 수 없습니다";
        NotFoundException exception = new NotFoundException(expectedMessage);

        // when
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleNotFoundException(exception);

        // then
        assertHttpStatus(response, HttpStatus.NOT_FOUND, expectedMessage);
    }

    @Test
    @DisplayName("ConflictException을 409 Conflict로 처리한다")
    void handleConflictException() {
        // given
        String expectedMessage = "리소스 충돌이 발생했습니다";
        ConflictException exception = new ConflictException(expectedMessage);

        // when
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleConflictException(exception);

        // then
        assertHttpStatus(response, HttpStatus.CONFLICT, expectedMessage);
    }

    @Test
    @DisplayName("RuntimeException을 500 Internal Server Error로 처리한다")
    void handleRuntimeException() {
        // given
        String expectedMessage = GlobalExceptionHandler.DEFAULT_INTERNAL_SERVER_ERROR_MESSAGE;
        RuntimeException exception = new RuntimeException(expectedMessage);

        // when
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleRuntimeException(exception);

        // then
        assertHttpStatus(response, HttpStatus.INTERNAL_SERVER_ERROR, expectedMessage);
    }

    @Test
    @DisplayName("Exception을 500 Internal Server Error로 처리한다")
    void handleException() {
        // given
        String expectedMessage = GlobalExceptionHandler.DEFAULT_INTERNAL_SERVER_ERROR_MESSAGE;
        Exception exception = new Exception(expectedMessage);

        // when
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleException(exception);

        // then
        assertHttpStatus(response, HttpStatus.INTERNAL_SERVER_ERROR, expectedMessage);
    }

    @Test
    @DisplayName("기본 메시지가 있는 UnauthorizedException을 처리한다")
    void handleUnauthorizedExceptionWithDefaultMessage() {
        // given
        UnauthorizedException exception = new UnauthorizedException();

        // when
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleUnauthorizedException(exception);

        // then
        assertHttpStatus(response, HttpStatus.UNAUTHORIZED, "인증이 필요한 요청입니다.");
    }

    private void assertHttpStatus(ResponseEntity<ErrorResponse> response, HttpStatus unauthorized,
            String expectedMessage) {
        assertThat(response.getStatusCode()).isEqualTo(unauthorized);
        assertThat(response.getBody().getStatus()).isEqualTo(unauthorized.value());
        assertThat(response.getBody().getMessage()).isEqualTo(expectedMessage);
    }
}
