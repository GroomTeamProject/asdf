package io.goorm.team02.dto.orders;

import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        String orderNumber,
        Long userId,
        Long storeId,
        String deliveryAddress,
        String deliveryDetailAddress,
        String storeAddress,
        String storeDetailAddress,
        String phone,
        String orderMemo,
        int menuTotalAmount,
        int deliveryFee,
        int discountAmount,
        int totalAmount,
        String status,
        LocalDateTime orderedAt,
        LocalDateTime acceptedAt,
        LocalDateTime cookingStartedAt,
        LocalDateTime cookingCompletedAt,
        LocalDateTime pickedUpAt,
        LocalDateTime deliveredAt,
        LocalDateTime cancelledAt,
        String rejectReason,
        String cancelReason,
        int minCookingTime,
        int maxCookingTime,
        List<OrderItemResponse> orderItems) {

    public record OrderItemResponse(
            Long id,
            Long menuId,
            String menuName,
            int menuPrice,
            int quantity,
            int totalPrice,
            List<OrderItemOptionResponse> options) {
    }

    public record OrderItemOptionResponse(
            Long id,
            String optionName,
            String optionItemName,
            int additionalPrice) {
    }
}