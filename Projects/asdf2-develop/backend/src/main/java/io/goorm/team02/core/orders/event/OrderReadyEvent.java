package io.goorm.team02.core.orders.event;

import io.goorm.team02.core.orders.domain.Order;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 주문 준비 완료 이벤트
 * 주문이 준비 완료되면 발행됩니다.
 */
@Getter
public class OrderReadyEvent extends ApplicationEvent {
    
    private final Order order;
    private final Long customerId;
    private final Long storeId;
    private final String storeName;
    
    public OrderReadyEvent(Object source, Order order) {
        super(source);
        this.order = order;
        this.customerId = order.getUser().getId();
        this.storeId = order.getStore().getId();
        this.storeName = order.getStore().getName();
    }
    
    /**
     * 고객용 알림 메시지
     */
    public String getCustomerMessage() {
        return String.format(
            "음식이 준비되었습니다! 🍽️\n" +
            "주문번호: %d\n" +
            "가게: %s\n" +
            "곧 배달이 시작됩니다!",
            order.getId(),
            storeName
        );
    }
    
    /**
     * 가게용 알림 메시지
     */
    public String getStoreMessage() {
        return String.format(
            "음식이 준비되었습니다!\n" +
            "주문번호: %d\n" +
            "고객: %s\n" +
            "배달 준비를 해주세요!",
            order.getId(),
            order.getUser().getName()
        );
    }
}
