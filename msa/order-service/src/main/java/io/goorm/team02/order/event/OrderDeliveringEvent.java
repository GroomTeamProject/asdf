package io.goorm.team02.order.event;

import io.goorm.team02.order.entity.Order;
import lombok.Getter;

/**
 * 주문 배달 시작 이벤트
 * 주문 배달이 시작되면 발행됩니다.
 */
@Getter
public class OrderDeliveringEvent extends OrderBaseEvent {

    public OrderDeliveringEvent(Order order) {
        super("ORDER_DELIVERING", "order-service", order);
    }
}
