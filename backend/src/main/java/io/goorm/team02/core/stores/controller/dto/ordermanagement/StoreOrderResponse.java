package io.goorm.team02.core.stores.controller.dto.ordermanagement;

import io.goorm.team02.core.orders.domain.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record StoreOrderResponse(
        Long orderId,
        String orderNumber,
        String customerName,
        String customerPhone,
        OrderStatus status,
        BigDecimal totalAmount,
        LocalDateTime orderedAt,
        LocalDateTime acceptedAt,
        Integer minCookingTime,
        Integer maxCookingTime,
        String deliveryAddress,
        List<StoreOrderItemResponse> orderItems,
        boolean isNewOrder  // 신규 주문 여부
) {}