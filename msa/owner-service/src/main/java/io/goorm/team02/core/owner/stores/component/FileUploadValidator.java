package io.goorm.team02.core.owner.stores.component;

import io.goorm.team02.core.owner.stores.controller.FileValidationResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Component
@Slf4j
public class FileUploadValidator {

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final int MAX_FILENAME_LENGTH = 255;

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            ".jpg", ".jpeg", ".png", ".gif", ".webp"
    );

    private static final String[] DANGEROUS_CHARS = {
            "../", "..\\", "<", ">", "|", ":", "*", "?", "\"", "\0", "\n", "\r"
    };

    /**
     * 파일 전체 검증
     */
    public FileValidationResult validate(MultipartFile file, Long userId) {
        // 1. 기본 검증
        FileValidationResult basicResult = validateBasic(file, userId);
        if (!basicResult.isValid()) {
            return basicResult;
        }

        // 2. 크기 검증
        FileValidationResult sizeResult = validateFileSize(file, userId);
        if (!sizeResult.isValid()) {
            return sizeResult;
        }

        // 3. 타입 검증
        FileValidationResult typeResult = validateContentType(file, userId);
        if (!typeResult.isValid()) {
            return typeResult;
        }

        // 4. 파일명 검증
        FileValidationResult filenameResult = validateFilename(file, userId);
        if (!filenameResult.isValid()) {
            return filenameResult;
        }

        return FileValidationResult.success();
    }

    private FileValidationResult validateBasic(MultipartFile file, Long userId) {
        if (file == null || file.isEmpty()) {
            log.warn("빈 파일 업로드 시도 - 사용자 ID: {}", userId);
            return FileValidationResult.error(
                    HttpStatus.BAD_REQUEST,
                    "파일이 비어있습니다"
            );
        }
        return FileValidationResult.success();
    }

    private FileValidationResult validateFileSize(MultipartFile file, Long userId) {
        if (file.getSize() > MAX_FILE_SIZE) {
            log.warn("파일 크기 초과 - 사용자 ID: {}, 파일 크기: {} bytes",
                    userId, file.getSize());
            return FileValidationResult.error(
                    HttpStatus.PAYLOAD_TOO_LARGE,
                    "파일 크기는 10MB를 초과할 수 없습니다"
            );
        }
        return FileValidationResult.success();
    }

    private FileValidationResult validateContentType(MultipartFile file, Long userId) {
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            log.warn("허용되지 않은 파일 타입 - 사용자 ID: {}, Content-Type: {}",
                    userId, contentType);
            return FileValidationResult.error(
                    HttpStatus.BAD_REQUEST,
                    "지원하지 않는 파일 형식입니다. JPG, PNG, GIF, WEBP만 가능합니다"
            );
        }
        return FileValidationResult.success();
    }

    private FileValidationResult validateFilename(MultipartFile file, Long userId) {
        String originalFilename = file.getOriginalFilename();

        if (!isSecureFilename(originalFilename)) {
            log.warn("위험한 파일명 - 사용자 ID: {}, 파일명: {}",
                    userId, originalFilename);
            return FileValidationResult.error(
                    HttpStatus.BAD_REQUEST,
                    "올바르지 않은 파일명입니다"
            );
        }
        return FileValidationResult.success();
    }

    private boolean isSecureFilename(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            return false;
        }

        if (filename.length() > MAX_FILENAME_LENGTH) {
            return false;
        }

        // 위험한 문자 검증
        for (String dangerous : DANGEROUS_CHARS) {
            if (filename.contains(dangerous)) {
                return false;
            }
        }

        // 확장자 검증
        String lowerFilename = filename.toLowerCase();
        return ALLOWED_EXTENSIONS.stream()
                .anyMatch(lowerFilename::endsWith);
    }
}
