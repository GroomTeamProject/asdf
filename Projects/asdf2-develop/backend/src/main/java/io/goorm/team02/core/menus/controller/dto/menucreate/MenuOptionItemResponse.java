// MenuOptionItemResponse.java (완전 버전)
package io.goorm.team02.core.menus.controller.dto.menucreate;

import io.goorm.team02.core.menus.domain.MenuOptionItem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
@Schema(description = "메뉴 옵션 아이템 응답")
public class MenuOptionItemResponse {

    @Schema(description = "옵션 아이템 ID", example = "1")
    private Long id;

    @Schema(description = "옵션 아이템명", example = "대")
    private String name;

    @Schema(description = "추가 금액", example = "1000.00")
    private BigDecimal additionalPrice;

    @Schema(description = "옵션 아이템 표시 순서", example = "1")
    private Integer displayOrder;

    @Schema(description = "활성화 여부", example = "true")
    private Boolean isActive;

    public static MenuOptionItemResponse from(MenuOptionItem item) {
        return MenuOptionItemResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .additionalPrice(item.getAdditionalPrice())
                .displayOrder(item.getDisplayOrder())
                .isActive(item.getIsActive())
                .build();
    }
}