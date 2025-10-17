package io.goorm.team02.order.event;

import io.goorm.team02.order.entity.Order;
import lombok.Getter;

/**
 * 주문 수락 이벤트
 * 주문이 수락되면 발행됩니다.
 */
@Getter
public class OrderAcceptedEvent extends OrderBaseEvent {

    private final Integer minCookingTime;
    private final Integer maxCookingTime;

    public OrderAcceptedEvent(Order order) {
        super("ORDER_ACCEPTED", "order-service", order);
        this.minCookingTime = order.getMinCookingTime();
        this.maxCookingTime = order.getMaxCookingTime();
    }
}
