package io.goorm.team02.core.menus.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

@Service
public class SecureFileValidationService {

    // 이미지 파일 매직 바이트 정의
    private static final byte[][] IMAGE_MAGIC_BYTES = {
            {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF},           // JPEG
            {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A}, // PNG
            {0x47, 0x49, 0x46, 0x38, 0x37, 0x61},             // GIF87a
            {0x47, 0x49, 0x46, 0x38, 0x39, 0x61},             // GIF89a
            {0x52, 0x49, 0x46, 0x46}                          // WEBP (RIFF)
    };

    private static final String[] ALLOWED_EXTENSIONS = {".jpg", ".jpeg", ".png", ".gif", ".webp"};
    private static final String[] ALLOWED_CONTENT_TYPES = {
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    };

    /**
     * 강화된 파일 검증 (매직 바이트 + 확장자 + Content-Type)
     */
    public void validateImageFile(MultipartFile file) {
        // 1. 기본 검증
        if (file == null || file.isEmpty()) {
            throw new SecurityException("파일이 비어있습니다");
        }

        // 2. 파일 크기 검증 (5MB로 제한 강화)
        long maxSize = 5 * 1024 * 1024; // 5MB
        if (file.getSize() > maxSize) {
            throw new SecurityException("파일 크기는 5MB를 초과할 수 없습니다");
        }

        // 3. 파일명 보안 검증
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !isSecureFilename(originalFilename)) {
            throw new SecurityException("안전하지 않은 파일명입니다");
        }

        // 4. Content-Type 검증
        String contentType = file.getContentType();
        if (!isAllowedContentType(contentType)) {
            throw new SecurityException("허용되지 않는 파일 형식입니다");
        }

        // 5. 파일 확장자 검증
        if (!isAllowedExtension(originalFilename)) {
            throw new SecurityException("허용되지 않는 파일 확장자입니다");
        }

        // 6. ⭐ 매직 바이트 검증 (가장 중요!)
        if (!isValidImageMagicBytes(file)) {
            throw new SecurityException("이미지 파일이 아닙니다");
        }

        // 7. 파일 내용 스캔 (악성코드 패턴 검사)
        scanFileContent(file);
    }

    /**
     * 매직 바이트로 실제 파일 형식 검증
     */
    private boolean isValidImageMagicBytes(MultipartFile file) {
        try (InputStream is = file.getInputStream()) {
            byte[] header = new byte[12]; // 충분한 크기로 읽기
            int bytesRead = is.read(header);

            if (bytesRead < 4) {
                return false;
            }

            // 각 이미지 형식의 매직 바이트와 비교
            for (byte[] magicBytes : IMAGE_MAGIC_BYTES) {
                if (bytesRead >= magicBytes.length) {
                    boolean matches = true;
                    for (int i = 0; i < magicBytes.length; i++) {
                        if (header[i] != magicBytes[i]) {
                            matches = false;
                            break;
                        }
                    }
                    if (matches) {
                        // WEBP 추가 검증
                        if (magicBytes.length == 4 && Arrays.equals(magicBytes, new byte[]{0x52, 0x49, 0x46, 0x46})) {
                            // WEBP는 8-11번째 바이트가 "WEBP"여야 함
                            if (bytesRead >= 12) {
                                String webpSignature = new String(Arrays.copyOfRange(header, 8, 12));
                                return "WEBP".equals(webpSignature);
                            }
                        } else {
                            return true;
                        }
                    }
                }
            }
            return false;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 파일명 보안 검증
     */
    private boolean isSecureFilename(String filename) {
        // 경로 순회 공격 방지
        if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
            return false;
        }

        // 특수 문자 제한 (알파벳, 숫자, 점, 하이픈, 언더스코어만 허용)
        if (!filename.matches("^[a-zA-Z0-9._-]+$")) {
            return false;
        }

        // 파일명 길이 제한
        if (filename.length() > 100) {
            return false;
        }

        // 예약어 방지
        String[] reservedNames = {"CON", "PRN", "AUX", "NUL", "COM1", "LPT1"};
        String nameWithoutExt = filename.substring(0, filename.lastIndexOf('.'));
        return !Arrays.asList(reservedNames).contains(nameWithoutExt.toUpperCase());
    }

    /**
     * 파일 내용 악성코드 패턴 검사
     */
    private void scanFileContent(MultipartFile file) {
        try (InputStream is = file.getInputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead = is.read(buffer);

            if (bytesRead > 0) {
                String content = new String(buffer, 0, bytesRead).toLowerCase();

                // 악성 스크립트 패턴 검사
                String[] maliciousPatterns = {
                        "<?php", "<%", "<script", "javascript:", "eval(", "base64_decode"
                };

                for (String pattern : maliciousPatterns) {
                    if (content.contains(pattern)) {
                        throw new SecurityException("악성 코드가 포함된 파일입니다");
                    }
                }
            }
        } catch (IOException e) {
//            log.error("파일 내용 스캔 실패", e);
            throw new SecurityException("파일 스캔 중 오류가 발생했습니다");
        }
    }

    private boolean isAllowedContentType(String contentType) {
        return contentType != null &&
                Arrays.asList(ALLOWED_CONTENT_TYPES).contains(contentType.toLowerCase());
    }

    private boolean isAllowedExtension(String filename) {
        String lowerFilename = filename.toLowerCase();
        return Arrays.stream(ALLOWED_EXTENSIONS)
                .anyMatch(lowerFilename::endsWith);
    }
}