package io.goorm.team02.core.users.listner;

import io.goorm.team02.core.sse.service.SSEService;
import io.goorm.team02.core.orders.event.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자 알림 서비스
 * 주문 생성 및 상태 변경 시 사용자에게 알림을 보냅니다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UserNotificationListner {

    private final SSEService sseService;

    /**
     * 주문 생성 이벤트 리스너
     * 주문이 생성되면 사용자에게 주문 접수 알림을 보냅니다.
     */
    @EventListener
    @Async("eventTaskExecutor")
    public void handleOrderCreated(OrderCreatedEvent event) {
        Long userId = event.getCustomerId();
        String message = event.getCustomerMessage();

        sendRealTimeNotification(userId, message);
    }

    /**
     * 주문 수락 이벤트 리스너
     * 주문이 수락되면 사용자에게 주문 확인 알림을 보냅니다.
     */
    @EventListener
    @Async("eventTaskExecutor")
    public void handleOrderAccepted(OrderAcceptedEvent event) {
        Long userId = event.getCustomerId();
        String message = event.getCustomerMessage();

        sendRealTimeNotification(userId, message);
    }

    /**
     * 주문 조리 시작 이벤트 리스너
     * 주문 조리가 시작되면 사용자에게 알림을 보냅니다.
     */
    @EventListener
    @Async("eventTaskExecutor")
    public void handleOrderPreparing(OrderCookingEvent event) {
        Long userId = event.getCustomerId();
        String message = event.getCustomerMessage();

        sendRealTimeNotification(userId, message);
    }

    /**
     * 주문 준비 완료 이벤트 리스너
     * 주문이 준비 완료되면 사용자에게 알림을 보냅니다.
     */
    @EventListener
    @Async("eventTaskExecutor")
    public void handleOrderReady(OrderReadyEvent event) {
        Long userId = event.getCustomerId();
        String message = event.getCustomerMessage();

        sendRealTimeNotification(userId, message);
    }

    /**
     * 주문 배달 시작 이벤트 리스너
     * 주문 배달이 시작되면 사용자에게 알림을 보냅니다.
     */
    @EventListener
    @Async("eventTaskExecutor")
    public void handleOrderDelivering(OrderDeliveringEvent event) {
        Long userId = event.getCustomerId();
        String message = event.getCustomerMessage();

        sendRealTimeNotification(userId, message);
    }

    /**
     * 주문 배달 완료 이벤트 리스너
     * 주문이 배달 완료되면 사용자에게 알림을 보냅니다.
     */
    @EventListener
    @Async("eventTaskExecutor")
    public void handleOrderDelivered(OrderDeliveredEvent event) {
        Long userId = event.getCustomerId();
        String message = event.getCustomerMessage();

        sendRealTimeNotification(userId, message);
    }

    /**
     * 주문 취소 이벤트 리스너
     * 주문이 취소되면 사용자에게 알림을 보냅니다.
     */
    @EventListener
    @Async("eventTaskExecutor")
    public void handleOrderCancelled(OrderCancelledEvent event) {
        Long userId = event.getCustomerId();
        String message = event.getCustomerMessage();

        sendRealTimeNotification(userId, message);
    }

    /**
     * 주문 거절 이벤트 리스너
     * 주문이 거절되면 사용자에게 알림을 보냅니다.
     */
    @EventListener
    @Async("eventTaskExecutor")
    public void handleOrderRejected(OrderRejectedEvent event) {
        Long userId = event.getCustomerId();
        String message = event.getCustomerMessage();

        sendRealTimeNotification(userId, message);
    }

    /**
     * 실시간 알림 전송 (SSE)
     */
    private void sendRealTimeNotification(Long userId, String message) {
        sseService.sendCustomerNotification(userId, message);
    }

}
