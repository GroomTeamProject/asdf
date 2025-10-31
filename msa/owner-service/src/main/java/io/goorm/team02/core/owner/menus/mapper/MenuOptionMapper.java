package io.goorm.team02.core.owner.menus.mapper;

import io.goorm.team02.core.owner.menus.domain.MenuOption;
import io.goorm.team02.core.owner.menus.domain.enums.OptionType;
import io.goorm.team02.dto.owner.menus.menucreate.MenuOptionResponse;
import io.goorm.team02.dto.owner.menus.menucreate.MenuOptionGroupResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MenuOptionMapper {

    private final MenuOptionItemMapper menuOptionItemMapper;

    // ================================
    // OptionType 변환 메서드들
    // ================================

    /**
     * String을 OptionType enum으로 변환
     */
    public OptionType convertStringToOptionType(String type) {
        if (type == null || type.trim().isEmpty()) {
            return OptionType.SINGLE; // 기본값
        }

        try {
            return OptionType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("유효하지 않은 옵션 타입입니다: " + type);
        }
    }

    /**
     * OptionType enum을 String으로 변환
     */
    public String convertOptionTypeToString(OptionType type) {
        return type != null ? type.name() : "SINGLE";
    }

    // ================================
    // MenuOption 변환 메서드들
    // ================================

    /**
     * MenuOption Entity -> MenuOptionResponse DTO 변환
     */
    public MenuOptionResponse toResponse(MenuOption option) {
        int itemCount = 0;
        int activeItemCount = 0;

        if (option.getItems() != null) {
            itemCount = option.getItems().size();
            activeItemCount = (int) option.getItems().stream()
                    .filter(item -> item.getIsActive())
                    .count();
        }

        return MenuOptionResponse.builder()
                .id(option.getId())
                .name(option.getName())
                .type(convertOptionTypeToString(option.getType()))
                .isRequired(option.getIsRequired())
                .displayOrder(option.getDisplayOrder())
                .itemCount(itemCount)
                .activeItemCount(activeItemCount)
                .items(option.getItems() != null ?
                        menuOptionItemMapper.toResponseList(option.getItems()) : null)
                .build();
    }

    /**
     * MenuOption Entity -> MenuOptionGroupResponse DTO 변환
     */
    public MenuOptionGroupResponse toGroupResponse(MenuOption menuOption) {
        int totalItems = 0;
        int activeItems = 0;
        int inactiveItems = 0;

        if (menuOption.getItems() != null) {
            totalItems = menuOption.getItems().size();
            activeItems = (int) menuOption.getItems().stream()
                    .filter(item -> item.getIsActive())
                    .count();
            inactiveItems = totalItems - activeItems;
        }

        // 옵션 그룹 상태 결정
        MenuOptionGroupResponse.OptionGroupStatus status = determineOptionGroupStatus(
                menuOption, activeItems, totalItems);

        return MenuOptionGroupResponse.builder()
                .id(menuOption.getId())
                .menuId(menuOption.getMenu() != null ? menuOption.getMenu().getId() : null)
                .menuName(menuOption.getMenu() != null ? menuOption.getMenu().getName() : null)
                .name(menuOption.getName())
                .type(convertOptionTypeToString(menuOption.getType()))
                .isRequired(menuOption.getIsRequired())
                .displayOrder(menuOption.getDisplayOrder())
                .totalItems(totalItems)
                .activeItems(activeItems)
                .inactiveItems(inactiveItems)
                .items(menuOption.getItems() != null ?
                        menuOptionItemMapper.toResponseList(menuOption.getItems()) : null)
                .status(status)
                .build();
    }

    /**
     * List<MenuOption> -> List<MenuOptionResponse> 변환
     */
    public List<MenuOptionResponse> toResponseList(List<MenuOption> options) {
        return options.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * List<MenuOption> -> List<MenuOptionGroupResponse> 변환
     */
    public List<MenuOptionGroupResponse> toGroupResponseList(List<MenuOption> options) {
        return options.stream()
                .map(this::toGroupResponse)
                .collect(Collectors.toList());
    }

    // ================================
    // 유틸리티 메서드들
    // ================================

    /**
     * 옵션 그룹 상태 결정
     */
    private MenuOptionGroupResponse.OptionGroupStatus determineOptionGroupStatus(
            MenuOption menuOption, int activeItems, int totalItems) {

        boolean hasActiveItems = activeItems > 0;
        boolean isValid = totalItems > 0 && (!menuOption.getIsRequired() || hasActiveItems);

        String message;
        if (totalItems == 0) {
            message = "옵션 아이템이 없습니다";
        } else if (menuOption.getIsRequired() && activeItems == 0) {
            message = "필수 옵션에 활성화된 아이템이 없습니다";
        } else if (activeItems == 0) {
            message = "활성화된 아이템이 없습니다";
        } else {
            message = "정상";
        }

        return MenuOptionGroupResponse.OptionGroupStatus.builder()
                .hasActiveItems(hasActiveItems)
                .isValid(isValid)
                .message(message)
                .build();
    }
}