package io.goorm.team02.order.event;

import io.goorm.team02.kafka.model.BaseEvent;
import io.goorm.team02.order.entity.Order;
import lombok.Getter;

@Getter
public abstract class OrderBaseEvent extends BaseEvent {

    private final Order order;
    private final Long orderId;
    private final Long userId;
    private final Long storeId;

    public OrderBaseEvent(String eventType, String source, Order order) {
        super(eventType, source);
        this.order = order;
        this.orderId = order.getId();
        this.userId = order.getUserId();
        this.storeId = order.getStoreId();
    }
}
