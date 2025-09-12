package io.goorm.team02.core.reviews.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 리뷰 생성 요청 DTO
 */
@Schema(description = "리뷰 생성 요청")
public record ReviewRequest(
    @Schema(description = "사용자 ID", example = "1")
    @NotNull(message = "사용자 ID는 필수입니다")
    Long userId,

    @Schema(description = "주문 ID", example = "1")
    @NotNull(message = "주문 ID는 필수입니다")
    Long orderId,

    @Schema(description = "평점 (1~5)", example = "5")
    @NotNull(message = "평점은 필수입니다")
    @Min(value = 1, message = "평점은 1점 이상이어야 합니다")
    @Max(value = 5, message = "평점은 5점 이하여야 합니다")
    Integer rating,

    @Schema(description = "리뷰 내용", example = "맛있고 배달도 빨랐어요!")
    @Size(max = 1000, message = "리뷰 내용은 1000자를 초과할 수 없습니다")
    String content
) {
}
