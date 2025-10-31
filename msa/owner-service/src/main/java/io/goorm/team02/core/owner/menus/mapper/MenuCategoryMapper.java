package io.goorm.team02.core.owner.menus.mapper;

import io.goorm.team02.core.owner.menus.domain.MenuCategory;
import io.goorm.team02.core.owner.menus.domain.enums.MenuStatus;
import io.goorm.team02.dto.owner.menus.categorycreate.MenuCategoryResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MenuCategoryMapper {

    /**
     * MenuCategory Entity -> MenuCategoryResponse DTO 변환
     */
    public MenuCategoryResponse toResponse(MenuCategory category) {
        int totalMenuCount = 0;
        int activeMenuCount = 0;

        if (category.getMenus() != null) {
            totalMenuCount = category.getMenus().size();
            activeMenuCount = (int) category.getMenus().stream()
                    .filter(menu -> menu.getStatus() == MenuStatus.AVAILABLE)
                    .count();
        }

        return MenuCategoryResponse.builder()
                .id(category.getId())
                .storeId(category.getStore() != null ? category.getStore().getId() : null)
                .name(category.getName())
                .displayOrder(category.getDisplayOrder())
                .isActive(category.getIsActive())
                .menuCount(totalMenuCount)
                .activeMenuCount(activeMenuCount)
                .build();
    }

    /**
     * List<MenuCategory> -> List<MenuCategoryResponse> 변환
     */
    public List<MenuCategoryResponse> toResponseList(List<MenuCategory> categories) {
        return categories.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}