package io.goorm.team02.core.owner.common.validation;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.regex.Pattern;
import java.util.Arrays;

@Component
public class SecureInputValidator {

    // XSS 방지 패턴
    private static final Pattern XSS_PATTERN = Pattern.compile(
            ".*(<script[^>]*>.*?</script>|javascript:|on\\w+\\s*=|<\\s*/?\\s*(script|iframe|object|embed|form|input)[^>]*>).*",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );

    // SQL Injection 방지 패턴
    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
            ".*(union|select|insert|delete|update|drop|create|alter|exec|execute|sp_|xp_)\\s+.*",
            Pattern.CASE_INSENSITIVE
    );

    // 안전한 문자열 패턴 (한글, 영문, 숫자, 기본 특수문자)
    private static final Pattern SAFE_TEXT_PATTERN = Pattern.compile(
            "^[가-힣a-zA-Z0-9\\s\\-_.,!?()\\[\\]{}:;\"']+$"
    );

    // 가격 패턴 (숫자와 소수점만)
    private static final Pattern PRICE_PATTERN = Pattern.compile("^\\d+(\\.\\d{1,2})?$");

    /**
     * 메뉴명 보안 검증
     */
    public void validateMenuName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new SecurityException("메뉴명은 필수입니다");
        }

        String trimmed = name.trim();

        // 길이 검증
        if (trimmed.length() < 2 || trimmed.length() > 50) {
            throw new SecurityException("메뉴명은 2-50자 사이여야 합니다");
        }

        // XSS 공격 패턴 검사
        if (XSS_PATTERN.matcher(trimmed).matches()) {
            throw new SecurityException("안전하지 않은 문자가 포함되어 있습니다");
        }

        // SQL Injection 패턴 검사
        if (SQL_INJECTION_PATTERN.matcher(trimmed).matches()) {
            throw new SecurityException("안전하지 않은 문자가 포함되어 있습니다");
        }

        // 안전한 문자만 허용
        if (!SAFE_TEXT_PATTERN.matcher(trimmed).matches()) {
            throw new SecurityException("허용되지 않는 특수문자가 포함되어 있습니다");
        }

        // 금지어 검사
        validateProhibitedWords(trimmed);
    }

    /**
     * 설명 필드 보안 검증
     */
    public void validateDescription(String description) {
        if (description == null) return; // 선택사항

        String trimmed = description.trim();

        // 길이 검증
        if (trimmed.length() > 500) {
            throw new SecurityException("설명은 500자를 초과할 수 없습니다");
        }

        // XSS 공격 패턴 검사
        if (XSS_PATTERN.matcher(trimmed).matches()) {
            throw new SecurityException("안전하지 않은 문자가 포함되어 있습니다");
        }

        // HTML 태그 완전 제거 (일부 허용 태그 외)
        String allowedTagsRemoved = removeUnsafeTags(trimmed);
        if (!allowedTagsRemoved.equals(trimmed)) {
            throw new SecurityException("허용되지 않는 HTML 태그가 포함되어 있습니다");
        }
    }

    /**
     * 가격 보안 검증
     */
    public void validatePrice(BigDecimal price) {
        if (price == null) {
            throw new SecurityException("가격은 필수입니다");
        }

        // 가격 범위 검증
        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new SecurityException("가격은 0원 이상이어야 합니다");
        }

        if (price.compareTo(new BigDecimal("10000000")) > 0) {
            throw new SecurityException("가격은 1천만원을 초과할 수 없습니다");
        }

        // 소수점 자리 수 제한
        if (price.scale() > 2) {
            throw new SecurityException("가격은 소수점 둘째 자리까지만 입력 가능합니다");
        }

        // 가격 문자열 패턴 검증
        String priceStr = price.toString();
        if (!PRICE_PATTERN.matcher(priceStr).matches()) {
            throw new SecurityException("올바르지 않은 가격 형식입니다");
        }
    }

    /**
     * URL 보안 검증
     */
    public void validateImageUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            return; // 선택사항
        }

        String trimmed = imageUrl.trim();

        // URL 길이 제한
        if (trimmed.length() > 500) {
            throw new SecurityException("이미지 URL은 500자를 초과할 수 없습니다");
        }

        // HTTPS 강제
        if (!trimmed.startsWith("https://")) {
            throw new SecurityException("HTTPS URL만 허용됩니다");
        }

        // 허용된 도메인만 사용 (S3, CloudFront 등)
        if (!isAllowedImageDomain(trimmed)) {
            throw new SecurityException("허용되지 않는 도메인입니다");
        }

        // URL 인젝션 공격 방지
        if (trimmed.contains("javascript:") || trimmed.contains("data:") ||
                trimmed.contains("<script") || trimmed.contains("eval(")) {
            throw new SecurityException("안전하지 않은 URL입니다");
        }
    }

    /**
     * 금지어 검사
     */
    private void validateProhibitedWords(String text) {
        String[] prohibitedWords = {
                "admin", "administrator", "root", "system", "test",
                "null", "undefined", "script", "alert", "console"
        };

        String lowerText = text.toLowerCase();
        for (String word : prohibitedWords) {
            if (lowerText.contains(word)) {
                throw new SecurityException("사용할 수 없는 단어가 포함되어 있습니다");
            }
        }
    }

    /**
     * 허용된 이미지 도메인 검사
     */
    private boolean isAllowedImageDomain(String url) {
        String[] allowedDomains = {
                "s3.amazonaws.com",
                "cloudfront.net",
                "yourcompany.com"  // 실제 도메인으로 변경
        };

        return Arrays.stream(allowedDomains)
                .anyMatch(url::contains);
    }

    /**
     * 안전하지 않은 HTML 태그 제거
     */
    private String removeUnsafeTags(String input) {
        // 허용되는 태그만 남기고 나머지 제거
        String[] allowedTags = {"<b>", "</b>", "<i>", "</i>", "<br>", "<p>", "</p>"};

        String result = input;
        // 모든 HTML 태그 제거하되 허용된 태그는 유지
        result = result.replaceAll("<(?!/?(?:" + String.join("|", allowedTags) + "))[^>]*>", "");

        return result;
    }
}