package io.goorm.team02.core.menus.controller.dto.menucreate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "옵션 아이템 순서 변경 요청")
public class OptionItemOrderUpdateRequest {

    @NotNull(message = "이동할 옵션 아이템 ID는 필수입니다")
    @Schema(description = "이동할 옵션 아이템 ID", required = true, example = "1")
    private Long optionId;

    @NotNull(message = "새로운 위치는 필수입니다")
    @Min(value = 1, message = "새로운 위치는 1 이상이어야 합니다")
    @Schema(description = "새로운 위치 (1부터 시작)", required = true, example = "3")
    private Integer newPosition;

    @NotNull(message = "옵션 그룹 ID는 필수입니다")
    @Schema(description = "옵션 그룹 ID", required = true, example = "1")
    private Long groupId;

    @NotNull(message = "메뉴 ID는 필수입니다")
    @Schema(description = "메뉴 ID", required = true, example = "1")
    private Long menuId;

    @Schema(description = "순서 변경 사유", example = "관리자에 의한 순서 조정")
    private String reason;
}