package io.goorm.team02.dto.owner.menus.menucreate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

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

    @Schema(description = "옵션 타입",
            allowableValues = {"SINGLE", "MULTIPLE"},
            example = "SINGLE")
    private String type; // Enum을 String으로 변경

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

    // from 메서드와 determineStatus 메서드 제거 - Entity 의존성 완전 제거!
}