package io.goorm.team02.dto.owner.stores.ordermanagement;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "가게 주문 응답")
public record StoreOrderResponse(
        @Schema(description = "주문 ID", example = "1")
        Long orderId,

        @Schema(description = "주문 번호", example = "ORD-20240101-001")
        String orderNumber,

        @Schema(description = "고객명", example = "김고객")
        String customerName,

        @Schema(description = "고객 전화번호", example = "010-1234-5678")
        String customerPhone,

        @Schema(description = "주문 상태",
                allowableValues = {"PENDING", "ACCEPTED", "PREPARING", "READY", "COMPLETED", "CANCELLED"},
                example = "PENDING")
        String status, // Enum을 String으로 변경

        @Schema(description = "총 주문 금액", example = "25000.00")
        BigDecimal totalAmount,

        @Schema(description = "주문 일시")
        LocalDateTime orderedAt,

        @Schema(description = "주문 승인 일시")
        LocalDateTime acceptedAt,

        @Schema(description = "최소 조리 시간(분)", example = "15")
        Integer minCookingTime,

        @Schema(description = "최대 조리 시간(분)", example = "20")
        Integer maxCookingTime,

        @Schema(description = "배달 주소", example = "서울시 강남구 테헤란로 123")
        String deliveryAddress,

        @Schema(description = "주문 상품 목록")
        List<StoreOrderItemResponse> orderItems,

        @Schema(description = "신규 주문 여부", example = "true")
        boolean isNewOrder
) {}