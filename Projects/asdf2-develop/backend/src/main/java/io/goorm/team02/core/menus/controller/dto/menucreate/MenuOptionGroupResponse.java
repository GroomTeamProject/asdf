// MenuOptionGroupResponse.java (수정된 버전)
package io.goorm.team02.core.menus.controller.dto.menucreate;

import io.goorm.team02.core.menus.domain.MenuOption;
import io.goorm.team02.core.menus.domain.enums.OptionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Builder
@Schema(description = "메뉴 옵션 그룹 응답")
public class MenuOptionGroupResponse {

    @Schema(description = "옵션 그룹 ID", example = "1")
    private Long id;

    @Schema(description = "메뉴 ID", example = "1")
    private Long menuId;

    @Schema(description = "메뉴명", example = "김치찌개")
    private String menuName;

    @Schema(description = "옵션 그룹명", example = "사이즈")
    private String name;

    @Schema(description = "옵션 타입", example = "SINGLE")
    private OptionType type;

    @Schema(description = "필수 옵션 여부", example = "true")
    private Boolean isRequired;

    @Schema(description = "옵션 그룹 표시 순서", example = "1")
    private Integer displayOrder;

    @Schema(description = "총 옵션 아이템 개수", example = "3")
    private Integer totalItems;

    @Schema(description = "활성화된 옵션 아이템 개수", example = "2")
    private Integer activeItems;

    @Schema(description = "비활성화된 옵션 아이템 개수", example = "1")
    private Integer inactiveItems;

    @Schema(description = "옵션 아이템 목록")
    private List<MenuOptionItemResponse> items;

    @Schema(description = "옵션 그룹 상태 정보")
    private OptionGroupStatus status;

    @Getter
    @Builder
    @Schema(description = "옵션 그룹 상태 정보")
    public static class OptionGroupStatus {
        @Schema(description = "사용 가능한 옵션이 있는지 여부", example = "true")
        private Boolean hasActiveItems;

        @Schema(description = "옵션 그룹이 정상적으로 구성되었는지 여부", example = "true")
        private Boolean isValid;

        @Schema(description = "상태 메시지", example = "정상")
        private String message;
    }

    public static MenuOptionGroupResponse from(MenuOption menuOption) {
        int totalItems = 0;
        int activeItems = 0;
        int inactiveItems = 0;
        List<MenuOptionItemResponse> items = null;

        if (menuOption.getItems() != null) {
            totalItems = menuOption.getItems().size();
            activeItems = (int) menuOption.getItems().stream()
                    .filter(item -> item.getIsActive())
                    .count();
            inactiveItems = totalItems - activeItems;

            items = menuOption.getItems().stream()
                    .map(MenuOptionItemResponse::from)
                    .toList();
        }

        // 옵션 그룹 상태 결정
        OptionGroupStatus status = determineStatus(menuOption, activeItems, totalItems);

        return MenuOptionGroupResponse.builder()
                .id(menuOption.getId())
                .menuId(menuOption.getMenu() != null ? menuOption.getMenu().getId() : null)
                .menuName(menuOption.getMenu() != null ? menuOption.getMenu().getName() : null)
                .name(menuOption.getName())
                .type(menuOption.getType())
                .isRequired(menuOption.getIsRequired())
                .displayOrder(menuOption.getDisplayOrder())
                .totalItems(totalItems)
                .activeItems(activeItems)
                .inactiveItems(inactiveItems)
                .items(items)
                .status(status)
                .build();
    }

    private static OptionGroupStatus determineStatus(MenuOption menuOption, int activeItems, int totalItems) {
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

        return OptionGroupStatus.builder()
                .hasActiveItems(hasActiveItems)
                .isValid(isValid)
                .message(message)
                .build();
    }
}