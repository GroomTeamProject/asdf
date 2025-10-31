package io.goorm.team02.dto.owner.stores.storemanagement;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "가게 운영시간 응답")
public class StoreHourResponse {

    @Schema(description = "운영시간 ID", example = "1")
    private Long id;

    @Schema(description = "요일 (1=월요일, 7=일요일)", example = "1")
    private Integer dayOfWeek;

    @Schema(description = "오픈 시간", example = "09:00")
    private LocalTime openTime;

    @Schema(description = "마감 시간", example = "22:00")
    private LocalTime closeTime;

    @Schema(description = "휴무 여부", example = "false")
    private Boolean isClosed;

    // from 메서드 제거 - Entity 의존성 완전 제거!
}