package io.goorm.team02.dto.owner.stores.storemanagement;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "가게 상태 변경 요청")
public class StoreStatusRequest {

    @NotNull(message = "가게 상태는 필수입니다")
    @Schema(description = "변경할 가게 상태",
            required = true,
            allowableValues = {"OPEN", "CLOSED", "TEMPORARILY_CLOSED", "BREAK"},
            example = "OPEN")
    private String status; // Enum을 String으로 변경

    @Size(max = 200, message = "상태 변경 사유는 200자를 초과할 수 없습니다")
    @Schema(description = "상태 변경 사유", example = "정기 휴무로 인한 임시 휴업")
    private String message;
}