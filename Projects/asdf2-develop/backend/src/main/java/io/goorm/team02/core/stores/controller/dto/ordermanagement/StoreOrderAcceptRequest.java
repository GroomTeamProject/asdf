package io.goorm.team02.core.stores.controller.dto.ordermanagement;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record StoreOrderAcceptRequest(
        @Schema(description = "최소 조리 시간 (분)", example = "20")
        @NotNull @Min(1) @Max(180)
        Integer minCookingTime,

        @Schema(description = "최대 조리 시간 (분)", example = "30")
        @NotNull @Min(1) @Max(180)
        Integer maxCookingTime
) {}
