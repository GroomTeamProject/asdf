package io.goorm.team02.dto.owner.stores.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "최근 주문 정보")
public record RecentOrderInfo(
        @Schema(description = "주문 ID", example = "123")
        Long orderId,

        @Schema(description = "주문 번호", example = "ORD-20241211-001")
        String orderNumber,

        @Schema(description = "고객명", example = "김철수")
        String customerName,

        @Schema(description = "주문 금액", example = "25000")
        BigDecimal orderAmount,

        @Schema(description = "주문 상태", example = "PENDING")
        String orderStatus,

        @Schema(description = "주문 시간", example = "2024-12-11T14:30:00")
        java.time.LocalDateTime orderedAt,

        @Schema(description = "긴급 주문 여부 (30분 이상 대기)", example = "true")
        Boolean isUrgent
) {}
