package io.goorm.team02.core.orders.event;

import io.goorm.team02.core.orders.domain.Order;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 주문 수락 이벤트
 * 주문이 수락되면 발행됩니다.
 */
@Getter
public class OrderAcceptedEvent extends ApplicationEvent {
    
    private final Order order;
    private final Long customerId;
    private final Long storeId;
    private final Integer minCookingTime;
    private final Integer maxCookingTime;
    
    public OrderAcceptedEvent(Object source, Order order) {
        super(source);
        this.order = order;
        this.customerId = order.getUser().getId();
        this.storeId = order.getStore().getId();
        this.minCookingTime = order.getMinCookingTime();
        this.maxCookingTime = order.getMaxCookingTime();
    }
    
    /**
     * 고객용 알림 메시지
     */
    public String getCustomerMessage() {
        return String.format(
            "주문이 수락되었습니다! 🎉\n" +
            "주문번호: %d\n" +
            "가게: %s\n" +
            "예상 조리 시간: %d~%d분\n" +
            "곧 맛있는 음식이 완성됩니다!",
            order.getId(),
            order.getStore().getName(),
            minCookingTime,
            maxCookingTime
        );
    }

    /**
     * 가게용 알림 메시지
     */
    public String getStoreMessage() {
        return String.format(
            "새로운 주문이 수락되었습니다!\n" +
            "주문번호: %d\n" +
            "고객: %s\n" +
            "예상 조리 시간: %d~%d분",
            order.getId(),
            order.getUser().getName(),
            minCookingTime,
            maxCookingTime
        );
    }
}
