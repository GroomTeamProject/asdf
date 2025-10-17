package io.goorm.team02.core.orders.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 주문 취소 요청 DTO
 */
@Schema(description = "주문 취소 요청")
public record OrderCancelRequest(
    @Schema(description = "취소 사유", example = "고객 요청으로 인한 주문 취소")
    @NotBlank(message = "취소 사유는 필수입니다")
    @Size(max = 500, message = "취소 사유는 500자를 초과할 수 없습니다")
    String cancelReason
) {
}