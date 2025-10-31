package io.goorm.team02.dto.owner.stores.storemanagement;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "가게 상태 정보 응답")
public class StoreStatusResponse {

    @Schema(description = "가게 ID", example = "1")
    private Long storeId;

    @Schema(description = "가게명", example = "맛있는 한식당")
    private String storeName;

    @Schema(description = "가게 상태",
            allowableValues = {"OPEN", "CLOSED", "TEMPORARILY_CLOSED", "BREAK"},
            example = "OPEN")
    private String status; // OPEN, CLOSED, TEMPORARILY_CLOSED 등

    @Schema(description = "가게 활성화 여부", example = "true")
    private Boolean isActive; // 가게 활성화 여부

    @Schema(description = "현재 시간 기준 영업 중 여부", example = "true")
    private Boolean isCurrentlyOpen; // 현재 시간 기준 영업 중인지

    @Schema(description = "오늘 영업 상태 메시지", example = "11:00 - 22:00 영업중")
    private String currentDayStatus; // 오늘 영업 상태 메시지

    @Schema(description = "상태 마지막 업데이트 시간")
    private LocalDateTime lastUpdated; // 상태 마지막 업데이트 시간

    // from 메서드들 제거 - Entity 의존성 완전 제거!
}