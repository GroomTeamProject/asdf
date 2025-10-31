package io.goorm.team02.core.owner.stores.controller;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FileValidationResult {
    private final boolean valid;
    private final HttpStatus status;
    private final String message;

    public static FileValidationResult success() {
        return new FileValidationResult(true, HttpStatus.OK, null);
    }

    public static FileValidationResult error(HttpStatus status, String message) {
        return new FileValidationResult(false, status, message);
    }
}