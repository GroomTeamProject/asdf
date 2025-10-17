package io.goorm.team02.core.orders.event;

import io.goorm.team02.core.orders.domain.Order;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 주문 취소 이벤트
 * 주문이 취소되면 발행됩니다.
 */
@Getter
public class OrderCancelledEvent extends ApplicationEvent {
    
    private final Order order;
    private final Long customerId;
    private final Long storeId;
    private final String storeName;
    private final String cancelReason;
    
    public OrderCancelledEvent(Object source, Order order, String cancelReason) {
        super(source);
        this.order = order;
        this.customerId = order.getUser().getId();
        this.storeId = order.getStore().getId();
        this.storeName = order.getStore().getName();
        this.cancelReason = cancelReason;
    }
    
    /**
     * 고객용 알림 메시지
     */
    public String getCustomerMessage() {
        return String.format(
            "주문이 취소되었습니다. 😔\n" +
            "주문번호: %d\n" +
            "가게: %s\n" +
            "취소 사유: %s\n" +
            "환불 처리는 영업일 기준 1-3일 소요됩니다.",
            order.getId(),
            storeName,
            cancelReason
        );
    }
    
    /**
     * 가게용 알림 메시지
     */
    public String getStoreMessage() {
        return String.format(
            "주문이 취소되었습니다!\n" +
            "주문번호: %d\n" +
            "고객: %s\n" +
            "취소 사유: %s",
            order.getId(),
            order.getUser().getName(),
            cancelReason
        );
    }
}
