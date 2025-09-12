package io.goorm.team02.core.orders.controller.dto;

import io.goorm.team02.core.orders.domain.Order;
import io.goorm.team02.core.orders.domain.OrderItem;
import io.goorm.team02.core.orders.domain.OrderItemOption;
import io.goorm.team02.core.orders.domain.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        String orderNumber,
        Long userId,
        String userName,
        Long storeId,
        String storeName,
        String deliveryAddress,
        String deliveryDetailAddress,
        String phone,
        String orderMemo,
        BigDecimal menuTotalAmount,
        BigDecimal deliveryFee,
        BigDecimal discountAmount,
        BigDecimal totalAmount,
        OrderStatus status,
        LocalDateTime orderedAt,
        LocalDateTime acceptedAt,
        LocalDateTime cookingStartedAt,
        LocalDateTime cookingCompletedAt,
        LocalDateTime deliveredAt,
        LocalDateTime cancelledAt,
        String rejectReason,
        String cancelReason,
        Integer minCookingTime,
        Integer maxCookingTime,
        List<OrderItemResponse> orderItems
) {

    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getUser() != null ? order.getUser().getId() : null,
                order.getUser() != null ? order.getUser().getName() : null,
                order.getStore() != null ? order.getStore().getId() : null,
                order.getStore() != null ? order.getStore().getName() : null,
                order.getDeliveryAddress(),
                order.getDeliveryDetailAddress(),
                order.getPhone(),
                order.getOrderMemo(),
                order.getMenuTotalAmount(),
                order.getDeliveryFee(),
                order.getDiscountAmount(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getOrderedAt(),
                order.getAcceptedAt(),
                order.getCookingStartedAt(),
                order.getCookingCompletedAt(),
                order.getDeliveredAt(),
                order.getCancelledAt(),
                order.getRejectReason(),
                order.getCancelReason(),
                order.getMinCookingTime(),
                order.getMaxCookingTime(),
                order.getOrderItems().stream()
                        .map(OrderItemResponse::from)
                        .toList()
        );
    }

    public record OrderItemResponse(
            Long id,
            Long menuId,
            String menuName,
            BigDecimal menuPrice,
            Integer quantity,
            BigDecimal totalPrice,
            List<OrderItemOptionResponse> options
    ) {

        public static OrderItemResponse from(OrderItem orderItem) {
            return new OrderItemResponse(
                    orderItem.getId(),
                    orderItem.getMenu().getId(),
                    orderItem.getMenuName(),
                    orderItem.getMenuPrice(),
                    orderItem.getQuantity(),
                    orderItem.getTotalPrice(),
                    orderItem.getOptions().stream()
                            .map(OrderItemOptionResponse::from)
                            .toList()
            );
        }
    }

    public record OrderItemOptionResponse(
            Long id,
            String optionName,
            String optionItemName,
            BigDecimal additionalPrice
    ) {

        public static OrderItemOptionResponse from(OrderItemOption option) {
            return new OrderItemOptionResponse(
                    option.getId(),
                    option.getOptionName(),
                    option.getOptionItemName(),
                    option.getAdditionalPrice()
            );
        }
    }
}