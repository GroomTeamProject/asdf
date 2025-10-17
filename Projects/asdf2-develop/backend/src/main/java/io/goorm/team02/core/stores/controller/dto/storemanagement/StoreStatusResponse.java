package io.goorm.team02.core.stores.controller.dto.storemanagement;

import io.goorm.team02.core.stores.domain.Store;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreStatusResponse {
    private Long storeId;
    private String storeName;
    private String status; // OPEN, CLOSED, TEMPORARILY_CLOSED 등
    private Boolean isActive; // 가게 활성화 여부
    private Boolean isCurrentlyOpen; // 현재 시간 기준 영업 중인지
    private String currentDayStatus; // 오늘 영업 상태 메시지
    private LocalDateTime lastUpdated; // 상태 마지막 업데이트 시간

    public static StoreStatusResponse from(Store store, boolean isCurrentlyOpen, String currentDayStatus) {
        return StoreStatusResponse.builder()
            .storeId(store.getId())
            .storeName(store.getName())
            .status(store.getStatus() != null ? store.getStatus().toString() : null)
            .isActive(store.getIsActive())
            .isCurrentlyOpen(isCurrentlyOpen)
            .currentDayStatus(currentDayStatus)
            .lastUpdated(LocalDateTime.now())
            .build();
    }

    // 간단한 팩토리 메서드
    public static StoreStatusResponse from(Store store, boolean isCurrentlyOpen) {
        return from(store, isCurrentlyOpen, null);
    }
}