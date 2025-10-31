package io.goorm.team02.order.event;

import io.goorm.team02.order.entity.Order;
import lombok.Getter;

/**
 * 주문 준비 완료 이벤트
 * 주문이 준비 완료되면 발행됩니다.
 */
@Getter
public class OrderReadyEvent extends OrderBaseEvent {

    public OrderReadyEvent(Order order) {
        super("ORDER_READY", "order-service", order);
    }
}
