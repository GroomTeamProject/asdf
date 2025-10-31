package io.goorm.team02.order.service;

import org.springframework.stereotype.Service;

import io.goorm.team02.dto.orders.OrderResponseForDelivery;
import io.goorm.team02.order.entity.Order;

@Service
public class OrderMapper {
    public OrderResponseForDelivery toResponseForDelivery(Order order) {
        return new OrderResponseForDelivery(
                order.getId(),
                order.getOrderNumber(),
                order.getUserId(),
                order.getStoreId(),
                order.getStoreName(),
                order.getStorePhone(),
                order.getDeliveryAddress(),
                order.getDeliveryDetailAddress(),
                order.getStoreAddress(),
                order.getStoreDetailAddress(),
                order.getPhone(),
                order.getOrderMemo());
    }
}
