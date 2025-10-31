package io.goorm.team02.dto.owner.menus.menucreate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "메뉴 옵션 그룹 응답")
public class MenuOptionResponse {

    @Schema(description = "옵션 그룹 ID", example = "1")
    private Long id;

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

    @Schema(description = "옵션 아이템 개수", example = "3")
    private Integer itemCount;

    @Schema(description = "활성 옵션 아이템 개수", example = "2")
    private Integer activeItemCount;

    @Schema(description = "옵션 아이템 목록")
    private List<MenuOptionItemResponse> items;

    // from 메서드 제거 - Entity 의존성 완전 제거!
}