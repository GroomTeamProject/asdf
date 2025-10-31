package io.goorm.team02.dto.owner.menus.menucreate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "메뉴 상태 변경 요청")
public class MenuStatusRequest {

    @NotNull(message = "메뉴 상태는 필수입니다")
    @Schema(description = "메뉴 판매 상태",
            required = true,
            allowableValues = {"AVAILABLE", "SOLD_OUT", "HIDDEN"},
            example = "SOLD_OUT")
    private String status; // Enum을 String으로 변경

    @Schema(description = "상태 변경 사유", example = "재료 소진으로 인한 품절")
    private String reason;

    @Schema(description = "일시적 변경 여부 (true: 임시 변경, false: 영구 변경)", example = "true")
    private Boolean isTemporary = false;
}