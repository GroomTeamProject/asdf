package io.goorm.team02.order.event;

import io.goorm.team02.order.entity.Order;
import lombok.Getter;

/**
 * 주문 생성 이벤트
 * 주문이 생성되면 발행됩니다.
 */
@Getter
public class OrderCreatedEvent extends OrderBaseEvent {

    public OrderCreatedEvent(Order order) {
        super("ORDER_CREATED", "order-service", order);
    }
}
