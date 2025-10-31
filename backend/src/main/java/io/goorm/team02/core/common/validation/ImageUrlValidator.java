package io.goorm.team02.core.common.validation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ImageUrlValidator {

    @Value("${cloud.aws.region.static}")
    private String awsRegion;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    /**
     * 이미지 URL 검증
     */
    public String validateImageUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            return null;
        }

        String trimmedUrl = imageUrl.trim();

        // HTTPS URL만 허용
        if (!trimmedUrl.startsWith("https://")) {
            log.warn("HTTP URL 차단: {}", trimmedUrl);
            return null;
        }

        // 허용된 도메인 체크
        String[] allowedDomains = {
                bucketName + ".s3." + awsRegion + ".amazonaws.com",
                bucketName + ".s3.amazonaws.com", // 리전 없는 URL도 허용
                "d1bhs0w09pq7lg.cloudfront.net" // CloudFront 도메인 (있다면)
        };

        boolean isAllowed = false;
        for (String domain : allowedDomains) {
            if (trimmedUrl.contains(domain)) {
                isAllowed = true;
                break;
            }
        }

        if (!isAllowed) {
            log.warn("허용되지 않는 도메인: {}", trimmedUrl);
        }

        return isAllowed ? trimmedUrl : null;
    }

    /**
     * 개발 환경에서 추가 도메인 허용
     */
    public String validateImageUrlWithDevSupport(String imageUrl) {
        // 기본 검증 먼저 수행
        String validatedUrl = validateImageUrl(imageUrl);
        if (validatedUrl != null) {
            return validatedUrl;
        }

        // 개발 환경에서만 localhost 허용
        if (isDevEnvironment() && imageUrl != null) {
            if (imageUrl.startsWith("https://localhost:") ||
                    imageUrl.startsWith("https://127.0.0.1:")) {
                return imageUrl;
            }
        }

        return null;
    }

    private boolean isDevEnvironment() {
        String profile = System.getProperty("spring.profiles.active", "");
        return profile.contains("local") || profile.contains("dev");
    }
}
