package io.goorm.team02.dto.owner.stores.storemanagement;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "휴무일 등록 요청")
public class StoreHolidayRequest {

    @Schema(description = "휴무일", example = "2025-12-25", type = "string", format = "date")
    private LocalDate date;

    @Schema(description = "휴무 사유", example = "크리스마스", maxLength = 100)
    private String reason;

    @Schema(description = "매년 반복 여부", example = "true", defaultValue = "false")
    private Boolean isRecurring = false;
}