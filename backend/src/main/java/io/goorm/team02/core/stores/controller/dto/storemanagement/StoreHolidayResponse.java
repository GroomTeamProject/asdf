package io.goorm.team02.core.stores.controller.dto.storemanagement;

import io.goorm.team02.core.stores.domain.StoreHoliday;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "휴무일 응답")
public class StoreHolidayResponse {

    @Schema(description = "휴무일 ID", example = "1")
    private Long id;

    @Schema(description = "휴무일", example = "2024-12-25")
    private LocalDate date;

    @Schema(description = "휴무 사유", example = "크리스마스")
    private String reason;

    @Schema(description = "매년 반복 여부", example = "true")
    private Boolean isRecurring;

    // Entity -> DTO 변환 메서드
    public static StoreHolidayResponse from(StoreHoliday holiday) {
        return StoreHolidayResponse.builder()
            .id(holiday.getId())
            .date(holiday.getDate())
            .reason(holiday.getReason())
            .isRecurring(holiday.getIsRecurring())
            .build();
    }
}