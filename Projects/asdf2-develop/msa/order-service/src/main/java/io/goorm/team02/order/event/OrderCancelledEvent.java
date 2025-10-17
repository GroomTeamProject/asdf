package io.goorm.team02.order.event;

import io.goorm.team02.order.entity.Order;
import lombok.Getter;

/**
 * 주문 취소 이벤트
 * 주문이 취소되면 발행됩니다.
 */
@Getter
public class OrderCancelledEvent extends OrderBaseEvent {

    private final String cancelReason;

    public OrderCancelledEvent(Order order, String cancelReason) {
        super("ORDER_CANCELLED", "order-service", order);
        this.cancelReason = cancelReason;
    }
}
