package io.goorm.team02.core.owner.menus.mapper;

import io.goorm.team02.core.owner.menus.domain.MenuOptionItem;
import io.goorm.team02.dto.owner.menus.menucreate.MenuOptionItemResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MenuOptionItemMapper {

    // ================================
    // MenuOptionItem 변환 메서드들
    // ================================

    /**
     * MenuOptionItem Entity -> MenuOptionItemResponse DTO 변환
     */
    public MenuOptionItemResponse toResponse(MenuOptionItem item) {
        return MenuOptionItemResponse.builder()
                .id(item.getId())
                .name(sanitizeItemName(item.getName()))
                .additionalPrice(item.getAdditionalPrice())
                .displayOrder(item.getDisplayOrder())
                .isActive(item.getIsActive())
                .build();
    }

    /**
     * List<MenuOptionItem> -> List<MenuOptionItemResponse> 변환
     */
    public List<MenuOptionItemResponse> toResponseList(List<MenuOptionItem> items) {
        return items.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ================================
    // 유틸리티 메서드들
    // ================================

    /**
     * 옵션 아이템명 검증 및 정제
     */
    private String sanitizeItemName(String name) {
        if (name == null) return null;

        // HTML 태그 제거
        String sanitized = name.replaceAll("<[^>]*>", "");

        // 특수 문자 이스케이프
        sanitized = sanitized.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");

        // 길이 제한
        if (sanitized.length() > 50) {
            sanitized = sanitized.substring(0, 47) + "...";
        }

        return sanitized;
    }
}