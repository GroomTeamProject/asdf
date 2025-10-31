package io.goorm.team02.order.event;

import io.goorm.team02.order.entity.Order;
import lombok.Getter;

/**
 * 주문 거절 이벤트
 * 주문이 거절되면 발행됩니다.
 */
@Getter
public class OrderRejectedEvent extends OrderBaseEvent {

    private final String rejectReason;

    public OrderRejectedEvent(Order order, String rejectReason) {
        super("ORDER_REJECTED", "order-service", order);
        this.rejectReason = rejectReason;
    }

}
