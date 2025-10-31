package io.goorm.team02.dto.owner.stores.ordermanagement;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record StoreOrderRejectRequest(
        @Schema(description = "거절 사유", example = "재료 부족")
        @NotBlank @Size(max = 500)
        String rejectReason
) {}
