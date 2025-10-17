// MenuOrderUpdateRequest.java
package io.goorm.team02.core.menus.controller.dto.menucreate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "메뉴 순서 변경 요청")
public class MenuOrderUpdateRequest {

    @NotNull(message = "이동할 메뉴 ID는 필수입니다")
    @Schema(description = "이동할 메뉴 ID", required = true, example = "1")
    private Long menuId;

    @NotNull(message = "새로운 위치는 필수입니다")
    @Schema(description = "새로운 위치 (1부터 시작)", required = true, example = "3")
    private Integer newPosition;

    @Schema(description = "카테고리 ID (특정 카테고리 내에서만 순서 변경)", example = "1")
    private Long categoryId;

    @Schema(description = "전체 메뉴 기준 순서 변경 여부 (true: 전체, false: 카테고리 내)", example = "false")
    private Boolean globalOrder = false;
}