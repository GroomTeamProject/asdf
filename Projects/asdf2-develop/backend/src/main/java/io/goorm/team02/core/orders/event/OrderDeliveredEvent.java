package io.goorm.team02.core.orders.event;

import io.goorm.team02.core.orders.domain.Order;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 주문 배달 완료 이벤트
 * 주문이 배달 완료되면 발행됩니다.
 */
@Getter
public class OrderDeliveredEvent extends ApplicationEvent {
    
    private final Order order;
    private final Long customerId;
    private final String customerName;
    private final String storeName;
    
    public OrderDeliveredEvent(Object source, Order order) {
        super(source);
        this.order = order;
        this.customerId = order.getUser().getId();
        this.customerName = order.getUser().getName();
        this.storeName = order.getStore().getName();
    }
    
    /**
     * 고객용 알림 메시지
     */
    public String getCustomerMessage() {
        return String.format(
            "배달이 완료되었습니다! 🍽️\n" +
            "주문번호: %d\n" +
            "가게: %s\n" +
            "맛있게 드세요!",
            order.getId(),
            storeName
        );
    }
    
    /**
     * 리뷰 요청 메시지
     */
    public String getReviewRequestMessage() {
        return String.format(
            "주문이 완료되었습니다!\n" +
            "맛있게 드셨나요? 리뷰를 남겨주세요! ⭐\n" +
            "주문번호: %d",
            order.getId()
        );
    }
    
    /**
     * 가게용 완료 알림
     */
    public String getStoreCompletionMessage() {
        return String.format(
            "주문이 성공적으로 완료되었습니다!\n" +
            "주문번호: %d\n" +
            "고객: %s",
            order.getId(),
            customerName
        );
    }
}
