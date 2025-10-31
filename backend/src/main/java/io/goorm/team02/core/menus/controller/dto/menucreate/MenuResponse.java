package io.goorm.team02.core.menus.controller.dto.menucreate;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.goorm.team02.core.common.validation.ImageUrlValidator;
import io.goorm.team02.core.menus.domain.Menu;
import io.goorm.team02.core.menus.domain.enums.MenuStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // null 값은 응답에서 제외
@Schema(description = "메뉴 응답")
public class MenuResponse {

    @Schema(description = "메뉴 ID", example = "1")
    private Long id;

    @Schema(description = "메뉴명", example = "김치찌개")
    private String name;

    @Schema(description = "메뉴 설명", example = "집에서 직접 담근 김치로 끓인 얼큰한 김치찌개")
    private String description;

    @Schema(description = "메뉴 가격", example = "8000.00")
    private BigDecimal price;

    // 보안 위험: 내부 ID 노출 제거
    // @Schema(description = "카테고리 ID", example = "1")
    // private Long categoryId;

    @Schema(description = "카테고리명", example = "메인")
    private String categoryName;

    @Schema(description = "메뉴 이미지 URL", example = "https://example.com/images/kimchi-jjigae.jpg")
    private String imageUrl;

    @Schema(description = "인기 메뉴 여부", example = "false")
    private Boolean isPopular;

    @Schema(description = "추천 메뉴 여부", example = "false")
    private Boolean isRecommended;

    @Schema(description = "메뉴 판매 상태", example = "AVAILABLE")
    private MenuStatus status;

    @Schema(description = "메뉴 표시 순서", example = "1")
    private Integer displayOrder;

    // 보안 위험: 내부 생성/수정 시간 제거 (또는 조건부 노출)
    // @Schema(description = "생성일시")
    // private LocalDateTime createdAt;

    // @Schema(description = "수정일시")
    // private LocalDateTime updatedAt;

    @Schema(description = "메뉴 옵션 목록")
    private List<MenuOptionResponse> options;

    /**
     * 팩토리 메서드에서 검증된 URL 사용
     */
    public static MenuResponse from(Menu menu, ImageUrlValidator urlValidator) {
        return MenuResponse.builder()
                .id(menu.getId())
                .name(sanitizeName(menu.getName()))
                .description(sanitizeDescription(menu.getDescription()))
                .price(menu.getPrice())
                .categoryName(menu.getCategory() != null ? menu.getCategory().getName() : null)
                .imageUrl(urlValidator.validateImageUrl(menu.getImageUrl())) // 검증된 URL 사용
                .isPopular(menu.getIsPopular())
                .isRecommended(menu.getIsRecommended())
                .status(menu.getStatus())
                .displayOrder(menu.getDisplayOrder())
                .options(menu.getOptions() != null ?
                        menu.getOptions().stream()
                                .map(MenuOptionResponse::from)
                                .toList() : null)
                .build();
    }

    // 기존 방식과의 호환성을 위한 오버로드 (검증 없이)
    public static MenuResponse from(Menu menu) {
        return MenuResponse.builder()
                .id(menu.getId())
                .name(sanitizeName(menu.getName()))
                .description(sanitizeDescription(menu.getDescription()))
                .price(menu.getPrice())
                .categoryName(menu.getCategory() != null ? menu.getCategory().getName() : null)
                .imageUrl(menu.getImageUrl()) // 원본 URL (검증 안함)
                .isPopular(menu.getIsPopular())
                .isRecommended(menu.getIsRecommended())
                .status(menu.getStatus())
                .displayOrder(menu.getDisplayOrder())
                .options(menu.getOptions() != null ?
                        menu.getOptions().stream()
                                .map(MenuOptionResponse::from)
                                .toList() : null)
                .build();
    }

    /**
     * XSS 방지를 위한 메뉴명 검증
     */
    private static String sanitizeName(String name) {
        if (name == null) return null;

        // HTML 태그 제거
        String sanitized = name.replaceAll("<[^>]*>", "");

        // 특수 문자 이스케이프
        sanitized = sanitized.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");

        return sanitized;
    }

    /**
     * 설명 필드 검증 및 길이 제한
     */
    private static String sanitizeDescription(String description) {
        if (description == null) return null;

        // 길이 제한 (클라이언트 성능 고려)
        if (description.length() > 200) {
            description = description.substring(0, 200) + "...";
        }

        // HTML 태그 제거
        return description.replaceAll("<[^>]*>", "");
    }

    /**
     * 이미지 URL 검증
     */
    private static String validateImageUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            return null;
        }

        // HTTPS URL만 허용
        if (!imageUrl.startsWith("https://")) {
            return null; // 또는 기본 이미지 URL 반환
        }

        // 허용된 도메인만 노출

        String[] allowedDomains = {
                "s3.${cloud.aws.region.static}.amazonaws.com"
        };

        boolean isAllowed = false;
        for (String domain : allowedDomains) {
            if (imageUrl.contains(domain)) {
                isAllowed = true;
                break;
            }
        }

        return isAllowed ? imageUrl : null;
    }
}