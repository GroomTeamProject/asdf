package io.goorm.team02.dto.owner.stores.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.List;

@Schema(description = "가게 대시보드 응답")
public record StoreDashboardResponse(
        @Schema(description = "오늘 통계")
        TodayStats todayStats,

        @Schema(description = "가게 정보")
        RestaurantInfo restaurant,

        @Schema(description = "운영시간 정보")
        List<StoreHourInfo> storeHours,

        @Schema(description = "최근 주문 목록")
        List<RecentOrderInfo> recentOrders
) {

        // 내부 record들
        @Schema(description = "오늘 통계")
        public record TodayStats(
                @Schema(description = "오늘 주문 개수", example = "15")
                Long orders,

                @Schema(description = "오늘 매출", example = "450000")
                BigDecimal revenue
        ) {}

        @Schema(description = "가게 정보")
        public record RestaurantInfo(
                @Schema(description = "가게 ID", example = "1")
                Long id,

                @Schema(description = "가게명", example = "맛있는 치킨집")
                String name,

                @Schema(description = "평점", example = "4.5")
                BigDecimal rating,

                @Schema(description = "리뷰 개수", example = "127")
                Long reviewCount,

                @Schema(description = "총 주문 개수", example = "1234")
                Long totalOrders
        ) {}

        @Schema(description = "운영시간 정보")
        public record StoreHourInfo(
                @Schema(description = "요일 (0:일요일, 1:월요일...)", example = "1")
                Integer dayOfWeek,

                @Schema(description = "오픈 시간", example = "11:00:00")
                String openTime,

                @Schema(description = "마감 시간", example = "23:00:00")
                String closeTime,

                @Schema(description = "휴무 여부", example = "false")
                Boolean isClosed
        ) {}

        @Schema(description = "최근 주문 정보")
        public record RecentOrderInfo(
                @Schema(description = "주문 ID", example = "123")
                Long id,

                @Schema(description = "주문 번호", example = "ORD-20241211-001")
                String orderNumber,

                @Schema(description = "고객명", example = "김철수")
                String customerName,

                @Schema(description = "주문 총액", example = "25000")
                BigDecimal total,

                @Schema(description = "주문 상태", example = "pending")
                String status,

                @Schema(description = "주문 시간", example = "2024-12-11T14:30:00")
                String orderTime,

                @Schema(description = "주문 아이템 목록")
                List<OrderItemInfo> items
        ) {}

        @Schema(description = "주문 아이템 정보")
        public record OrderItemInfo(
                @Schema(description = "메뉴명", example = "양념치킨")
                String name,

                @Schema(description = "수량", example = "2")
                Integer quantity
        ) {}
}