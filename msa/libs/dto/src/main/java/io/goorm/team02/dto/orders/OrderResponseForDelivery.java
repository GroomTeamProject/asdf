package io.goorm.team02.dto.orders;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 라이더 - 주문 상세 응답 DTO
 */
@Schema(description = "라이더 - 주문 상세 응답")
public record OrderResponseForDelivery(
        Long id,
        String orderNumber,
        Long userId,
        Long storeId,
        String storeName,
        String storePhone,
        String deliveryAddress,
        String deliveryDetailAddress,
        String storeAddress,
        String storeDetailAddress,
        String phone,
        String orderMemo) {
}
