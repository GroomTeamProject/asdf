package io.goorm.team02.order.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;

/**
 * 주문 수락 요청 DTO (예상 조리 시간 포함)
 */
@Schema(description = "주문 수락 요청 (예상 조리 시간 포함)")
public record OrderAcceptRequest(
    @Schema(description = "최소 조리 시간 (분)", example = "20")
    @NotNull(message = "최소 조리 시간은 필수입니다")
    @Min(value = 1, message = "최소 조리 시간은 1분 이상이어야 합니다")
    @Max(value = 180, message = "최소 조리 시간은 180분 이하여야 합니다")
    Integer minCookingTime,
    
    @Schema(description = "최대 조리 시간 (분)", example = "40")
    @NotNull(message = "최대 조리 시간은 필수입니다")
    @Min(value = 1, message = "최대 조리 시간은 1분 이상이어야 합니다")
    @Max(value = 180, message = "최대 조리 시간은 180분 이하여야 합니다")
    Integer maxCookingTime
) {
}
