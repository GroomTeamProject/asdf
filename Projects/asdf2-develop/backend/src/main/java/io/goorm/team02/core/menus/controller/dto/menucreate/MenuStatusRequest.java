// MenuStatusRequest.java
package io.goorm.team02.core.menus.controller.dto.menucreate;

import io.goorm.team02.core.menus.domain.enums.MenuStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "메뉴 상태 변경 요청")
public class MenuStatusRequest {

    @NotNull(message = "메뉴 상태는 필수입니다")
    @Schema(description = "메뉴 판매 상태", required = true, allowableValues = {"AVAILABLE", "SOLD_OUT", "HIDDEN"}, example = "SOLD_OUT")
    private MenuStatus status;

    @Schema(description = "상태 변경 사유", example = "재료 소진으로 인한 품절")
    private String reason;

    @Schema(description = "일시적 변경 여부 (true: 임시 변경, false: 영구 변경)", example = "true")
    private Boolean isTemporary = false;
}