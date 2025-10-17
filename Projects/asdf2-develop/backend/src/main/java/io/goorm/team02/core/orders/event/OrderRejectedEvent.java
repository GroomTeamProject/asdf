package io.goorm.team02.core.orders.event;

import io.goorm.team02.core.orders.domain.Order;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 주문 거절 이벤트
 * 주문이 거절되면 발행됩니다.
 */
@Getter
public class OrderRejectedEvent extends ApplicationEvent {
    
    private final Order order;
    private final Long customerId;
    private final Long storeId;
    private final String storeName;
    private final String rejectReason;
    
    public OrderRejectedEvent(Object source, Order order, String rejectReason) {
        super(source);
        this.order = order;
        this.customerId = order.getUser().getId();
        this.storeId = order.getStore().getId();
        this.storeName = order.getStore().getName();
        this.rejectReason = rejectReason;
    }
    
    /**
     * 고객용 알림 메시지
     */
    public String getCustomerMessage() {
        return String.format(
            "주문이 거절되었습니다. 😔\n" +
            "주문번호: %d\n" +
            "가게: %s\n" +
            "거절 사유: %s\n" +
            "다른 가게에서 주문해보세요!",
            order.getId(),
            storeName,
            rejectReason
        );
    }
    
    /**
     * 가게용 알림 메시지
     */
    public String getStoreMessage() {
        return String.format(
            "주문이 거절되었습니다!\n" +
            "주문번호: %d\n" +
            "고객: %s\n" +
            "거절 사유: %s",
            order.getId(),
            order.getUser().getName(),
            rejectReason
        );
    }
}
