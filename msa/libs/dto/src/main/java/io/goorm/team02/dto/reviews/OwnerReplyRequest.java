package io.goorm.team02.dto.reviews;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 사장님 답글 작성 요청 DTO
 */
@Schema(description = "사장님 답글 요청")
public record OwnerReplyRequest(
    @Schema(description = "답글 내용", example = "감사합니다! 다음에도 이용해 주세요.")
    @NotBlank(message = "답글 내용은 필수입니다")
    @Size(max = 500, message = "답글 내용은 500자를 초과할 수 없습니다")
    String reply
) {
}
