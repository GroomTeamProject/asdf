package io.goorm.team02.dto.owner.stores.ordermanagement;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "가게 주문 상세 응답")
public record StoreOrderDetailResponse(
        @Schema(description = "주문 ID", example = "1")
        Long orderId,

        @Schema(description = "주문 번호", example = "ORD-20240101-001")
        String orderNumber,

        @Schema(description = "고객명", example = "김고객")
        String customerName,

        @Schema(description = "고객 전화번호", example = "010-1234-5678")
        String customerPhone,

        @Schema(description = "배달 주소", example = "서울시 강남구 테헤란로 123")
        String deliveryAddress,

        @Schema(description = "배달 상세 주소", example = "456호 (현대빌딩)")
        String deliveryDetailAddress,

        @Schema(description = "주문 메모", example = "문 앞에 놓아주세요")
        String orderMemo,

        @Schema(description = "주문 상태",
                allowableValues = {"PENDING", "ACCEPTED", "PREPARING", "READY", "COMPLETED", "CANCELLED"},
                example = "ACCEPTED")
        String status, // Enum을 String으로 변경

        @Schema(description = "메뉴 총 금액", example = "23000.00")
        BigDecimal menuTotalAmount,

        @Schema(description = "배달비", example = "2000.00")
        BigDecimal deliveryFee,

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

        @Schema(description = "거절 사유", example = "재료 소진")
        String rejectReason,

        @Schema(description = "주문 상품 상세 목록")
        List<StoreOrderItemDetailResponse> orderItems
) {}