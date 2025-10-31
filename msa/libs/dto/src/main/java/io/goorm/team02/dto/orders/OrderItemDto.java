package io.goorm.team02.dto.orders;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "주문 아이템 정보")
public class OrderItemDto {

    @Schema(description = "메뉴명", example = "양념치킨")
    private String name;

    @Schema(description = "수량", example = "2")
    private Integer quantity;

    @Schema(description = "단가", example = "18000")
    private java.math.BigDecimal price;

    @Schema(description = "소계 (단가 × 수량)", example = "36000")
    private java.math.BigDecimal subtotal;

    @Schema(description = "메뉴 옵션", example = "순한맛")
    private String options;
}