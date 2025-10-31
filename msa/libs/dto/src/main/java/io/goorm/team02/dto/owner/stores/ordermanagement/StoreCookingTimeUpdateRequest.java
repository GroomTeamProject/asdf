package io.goorm.team02.dto.owner.stores.ordermanagement;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record StoreCookingTimeUpdateRequest(
        @Schema(description = "수정된 최소 조리 시간 (분)", example = "25")
        @NotNull @Min(1) @Max(180)
        Integer minCookingTime,

        @Schema(description = "수정된 최대 조리 시간 (분)", example = "35")
        @NotNull @Min(1) @Max(180)
        Integer maxCookingTime
) {}

