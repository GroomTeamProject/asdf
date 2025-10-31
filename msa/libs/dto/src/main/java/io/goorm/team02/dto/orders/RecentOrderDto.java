package io.goorm.team02.dto.orders;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "최근 주문 정보")
public class RecentOrderDto {

    @Schema(description = "주문 ID", example = "123")
    private Long id;

    @Schema(description = "주문 번호", example = "ORD-20241211-001")
    private String orderNumber;

    @Schema(description = "고객명", example = "김철수")
    private String customerName;

    @Schema(description = "주문 총액", example = "25000")
    private BigDecimal total;

    @Schema(description = "주문 상태", example = "DELIVERED",
            allowableValues = {"PENDING", "ACCEPTED", "COOKING", "READY", "PICKED_UP", "DELIVERED", "CANCELLED"})
    private String status;

    @Schema(description = "주문 시간", example = "2024-12-11T14:30:00")
    private LocalDateTime orderTime;

    @Schema(description = "주문 아이템 목록")
    private List<OrderItemDto> items;
}
