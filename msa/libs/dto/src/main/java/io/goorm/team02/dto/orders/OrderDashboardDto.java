package io.goorm.team02.dto.orders;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "주문 대시보드 데이터 응답")
public class OrderDashboardDto {

    @Schema(description = "오늘 주문 개수", example = "15")
    private Long todayOrderCount;

    @Schema(description = "오늘 매출 (배달 완료된 주문만)", example = "450000")
    private BigDecimal todayRevenue;

    @Schema(description = "총 주문 개수", example = "1234")
    private Long totalOrderCount;

    @Schema(description = "평균 평점", example = "4.5")
    private BigDecimal averageRating;

    @Schema(description = "리뷰 개수", example = "127")
    private Long reviewCount;

    @Schema(description = "최근 주문 목록")
    private List<RecentOrderDto> recentOrders;
}
