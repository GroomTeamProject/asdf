package io.goorm.team02.order.event;

import io.goorm.team02.order.entity.Order;
import lombok.Getter;

/**
 * 주문 배달 완료 이벤트
 * 주문이 배달 완료되면 발행됩니다.
 */
@Getter
public class OrderDeliveredEvent extends OrderBaseEvent {

    public OrderDeliveredEvent(Order order) {
        super("ORDER_DELIVERED", "order-service", order);
    }
}
