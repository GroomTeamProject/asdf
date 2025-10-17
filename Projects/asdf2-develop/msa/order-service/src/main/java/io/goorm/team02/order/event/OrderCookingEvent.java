package io.goorm.team02.order.event;

import io.goorm.team02.order.entity.Order;
import lombok.Getter;

/**
 * 주문 조리 시작 이벤트
 * 주문 조리가 시작되면 발행됩니다.
 */
@Getter
public class OrderCookingEvent extends OrderBaseEvent {

    public OrderCookingEvent(Order order) {
        super("ORDER_COOKING", "order-service", order);
    }
}
