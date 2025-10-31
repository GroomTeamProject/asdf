package io.goorm.team02.core.owner.menus.mapper;

import io.goorm.team02.core.owner.menus.domain.Menu;
import io.goorm.team02.core.owner.menus.domain.enums.MenuStatus;
import io.goorm.team02.dto.owner.menus.menucreate.MenuResponse;
import io.goorm.team02.dto.owner.menus.menucreate.MenuDetailResponse;
import io.goorm.team02.dto.owner.menus.menucreate.MenuStatusRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MenuMapper {

    private final MenuOptionMapper menuOptionMapper;

    // ================================
    // MenuStatus 변환 메서드들
    // ================================

    /**
     * String을 MenuStatus enum으로 변환
     */
    public MenuStatus convertStringToMenuStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            return MenuStatus.AVAILABLE; // 기본값
        }

        try {
            return MenuStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("유효하지 않은 메뉴 상태입니다: " + status);
        }
    }

    /**
     * MenuStatus enum을 String으로 변환
     */
    public String convertMenuStatusToString(MenuStatus status) {
        return status != null ? status.name() : "AVAILABLE";
    }

    // ================================
    // Menu 변환 메서드들
    // ================================

    /**
     * Menu Entity -> MenuResponse DTO 변환
     */
    public MenuResponse toResponse(Menu menu) {
        return MenuResponse.builder()
                .id(menu.getId())
                .name(sanitizeName(menu.getName()))
                .description(sanitizeDescription(menu.getDescription()))
                .price(menu.getPrice())
                .categoryName(menu.getCategory() != null ? menu.getCategory().getName() : null)
                .imageUrl(validateImageUrl(menu.getImageUrl()))
                .isPopular(menu.getIsPopular())
                .isRecommended(menu.getIsRecommended())
                .status(convertMenuStatusToString(menu.getStatus()))
                .displayOrder(menu.getDisplayOrder())
                .options(menu.getOptions() != null ?
                        menuOptionMapper.toResponseList(menu.getOptions()) : null)
                .build();
    }

    /**
     * Menu Entity -> MenuDetailResponse DTO 변환
     */
    public MenuDetailResponse toDetailResponse(Menu menu) {
        // 옵션 통계 계산
        int totalOptionGroups = 0;
        int requiredOptionGroups = 0;

        if (menu.getOptions() != null) {
            totalOptionGroups = menu.getOptions().size();
            requiredOptionGroups = (int) menu.getOptions().stream()
                    .filter(option -> option.getIsRequired())
                    .count();
        }

        // 카테고리 정보
        MenuDetailResponse.CategoryInfo categoryInfo = null;
        if (menu.getCategory() != null) {
            categoryInfo = MenuDetailResponse.CategoryInfo.builder()
                    .id(menu.getCategory().getId())
                    .name(menu.getCategory().getName())
                    .isActive(menu.getCategory().getIsActive())
                    .displayOrder(menu.getCategory().getDisplayOrder())
                    .build();
        }

        return MenuDetailResponse.builder()
                .id(menu.getId())
                .storeId(menu.getStore() != null ? menu.getStore().getId() : null)
                .storeName(menu.getStore() != null ? menu.getStore().getName() : null)
                .categoryId(menu.getCategory() != null ? menu.getCategory().getId() : null)
                .categoryName(menu.getCategory() != null ? menu.getCategory().getName() : null)
                .name(sanitizeName(menu.getName()))
                .description(sanitizeDescription(menu.getDescription()))
                .price(menu.getPrice())
                .imageUrl(validateImageUrl(menu.getImageUrl()))
                .isPopular(menu.getIsPopular())
                .isRecommended(menu.getIsRecommended())
                .status(convertMenuStatusToString(menu.getStatus()))
                .displayOrder(menu.getDisplayOrder())
                .totalOptionGroups(totalOptionGroups)
                .requiredOptionGroups(requiredOptionGroups)
                .createdAt(menu.getCreatedAt())
                .updatedAt(menu.getUpdatedAt())
                .options(menu.getOptions() != null ?
                        menuOptionMapper.toResponseList(menu.getOptions()) : null)
                .categoryInfo(categoryInfo)
                .build();
    }

    /**
     * List<Menu> -> List<MenuResponse> 변환
     */
    public List<MenuResponse> toResponseList(List<Menu> menus) {
        return menus.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ================================
    // 검증 메서드들
    // ================================

    /**
     * MenuStatusRequest 검증 및 변환
     */
    public MenuStatus validateAndConvertMenuStatus(MenuStatusRequest request) {
        if (request.getStatus() == null || request.getStatus().trim().isEmpty()) {
            throw new IllegalArgumentException("메뉴 상태는 필수입니다");
        }

        return convertStringToMenuStatus(request.getStatus());
    }

    /**
     * 상태 변경 사유 검증
     */
    public void validateStatusChangeReason(MenuStatusRequest request) {
        if (request.getReason() != null && request.getReason().length() > 200) {
            throw new IllegalArgumentException("변경 사유는 200자를 초과할 수 없습니다");
        }
    }

    // ================================
    // 보안 및 데이터 검증 메서드들
    // ================================

    /**
     * XSS 방지를 위한 메뉴명 검증
     */
    private String sanitizeName(String name) {
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
    private String sanitizeDescription(String description) {
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
    private String validateImageUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            return null;
        }

        // HTTPS URL만 허용
        if (!imageUrl.startsWith("https://")) {
            return null;
        }

        // 허용된 도메인만 노출
        String[] allowedDomains = {
                "s3.amazonaws.com",
                "cloudfront.net",
                "your-cdn-domain.com" // 실제 CDN 도메인으로 변경 필요
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