package io.goorm.team02.core.common.exception;

import io.goorm.team02.core.common.exception.dto.ErrorResponse;
import io.goorm.team02.core.common.exception.dto.client.NotFoundException;
import io.goorm.team02.core.common.exception.dto.client.ForbiddenException;
import io.goorm.team02.core.common.exception.dto.client.UnauthorizedException;
import io.goorm.team02.core.common.exception.dto.client.ConflictException;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Hidden
@RestControllerAdvice
public class GlobalExceptionHandler {
    public static final String DEFAULT_INTERNAL_SERVER_ERROR_MESSAGE = "서버 내부 오류가 발생했습니다.";

    /**
     * Http Status 400
     * 잘못된 요청
     */
    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<ErrorResponse> handleBadRequestException(Exception ex) {
        return createErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /**
     * Http Status 401
     * 인증이 필요한 요청
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(UnauthorizedException ex) {
        return createErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    /**
     * Http Status 403
     * 요청에 대한 권한이 충분하지 않음
     */
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbiddenException(ForbiddenException ex) {
        return createErrorResponse(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    /**
     * Http Status 404
     * 요청에 대한 항목을 찾을 수 없음
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException ex) {
        return createErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /**
     * Http Status 409
     * 요청된 리소스가 충돌
     */
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflictException(ConflictException ex) {
        return createErrorResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    /**
     * Http Status 500
     * 서버 내부 오류
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, DEFAULT_INTERNAL_SERVER_ERROR_MESSAGE);
    }

    /**
     * Http Status 500
     * 서버 내부 오류
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, DEFAULT_INTERNAL_SERVER_ERROR_MESSAGE);
    }

    private ResponseEntity<ErrorResponse> createErrorResponse(HttpStatus status, String message) {
        ErrorResponse response = ErrorResponse.of(status, message);
        return new ResponseEntity<>(response, status);
    }
}
