package io.goorm.team02.notification.service;

import io.goorm.team02.notification.entity.Notification;
import io.goorm.team02.notification.entity.enums.NotificationType;
import io.goorm.team02.notification.repository.NotificationRepository;
import io.goorm.team02.notification.dto.NotificationStats;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SSEService sseService;

    /**
     * 사용자 알림 페이징 조회
     */
    public Page<Notification> getUserNotifications(Long userId, Pageable pageable) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    /**
     * 사용자의 읽지 않은 알림 개수 조회
     */
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    /**
     * 사용자의 읽지 않은 알림 페이징 조회
     */
    public Page<Notification> getUnreadNotifications(Long userId, Pageable pageable) {
        return notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId, pageable);
    }

    /**
     * 특정 알림을 읽음 상태로 변경
     */
    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        notificationRepository.markAsRead(notificationId, userId);
    }

    /**
     * 사용자의 모든 알림을 읽음 상태로 변경
     */
    @Transactional
    public void markAllAsRead(Long userId) {
        notificationRepository.markAllAsRead(userId);
    }

    /**
     * 알림 통계 정보 조회
     */
    public NotificationStats getNotificationStats(Long userId) {
        long totalCount = notificationRepository.countByUserId(userId);
        long unreadCount = notificationRepository.countByUserIdAndIsReadFalse(userId);
        long readCount = totalCount - unreadCount;
        
        return new NotificationStats(totalCount, readCount, unreadCount);
    }

    public void handleOrderEvent(String eventType, Map<String, Object> eventData) {
        log.info("Handling order event: {}", eventType);
        switch (eventType) {
            case "ORDER_CREATED":
                processOrderCreatedEvent(eventData);
                break;
            case "ORDER_ACCEPTED":
                processOrderAcceptedEvent(eventData);
                break;
            case "ORDER_COOKING":
                processOrderCookingEvent(eventData);
                break;
            case "ORDER_READY":
                processOrderReadyEvent(eventData);
                break;
            case "ORDER_PICKED_UP":
                processOrderPickedUpEvent(eventData);
                break;
            case "ORDER_DELIVERED":
                processOrderDeliveredEvent(eventData);
                break;
            case "ORDER_CANCELLED":
                processOrderCancelledEvent(eventData);
                break;
            case "ORDER_REJECTED":
                processOrderRejectedEvent(eventData);
                break;
            default:
                log.warn("Unknown event type: {}", eventType);
                break;
        }
    }

    @Transactional
    private void processOrderCreatedEvent(Map<String, Object> eventData) {
        log.info("Processing order created event: {}", eventData);

        Long orderId = ((Number) eventData.get("orderId")).longValue();
        Long userId = ((Number) eventData.get("userId")).longValue();

        // 고객에게 주문 접수 알림
        createNotification(
                userId,
                NotificationType.ORDER_STATUS,
                "[주문 접수]",
                String.format("주문이 접수되었습니다.\n주문 번호: %d", orderId));

        // 가게에게 새 주문 알림
        // TODO: storeId -> ownerId로 알림 발송
        // createNotification(
        // storeId,
        // NotificationType.ORDER_STATUS,
        // "새로운 주문이 들어왔습니다",
        // String.format("주문이 접수되었습니다. 주문 번호: %d", orderId));
    }

    @Transactional
    private void processOrderAcceptedEvent(Map<String, Object> eventData) {
        log.info("Processing order accepted event: {}", eventData);

        Long orderId = ((Number) eventData.get("orderId")).longValue();
        Long userId = ((Number) eventData.get("userId")).longValue();
        Integer minCookingTime = (Integer) eventData.get("minCookingTime");
        Integer maxCookingTime = (Integer) eventData.get("maxCookingTime");

        // 고객에게 주문 수락 알림
        createNotification(
                userId,
                NotificationType.ORDER_STATUS,
                "[주문 수락]",
                String.format("주문이 수락되었습니다.\n주문 번호: %d\n예상 조리 시간: %d ~ %d분",
                        orderId, minCookingTime, maxCookingTime));
    }

    @Transactional
    private void processOrderCookingEvent(Map<String, Object> eventData) {
        log.info("Processing order cooking event: {}", eventData);

        Long orderId = ((Number) eventData.get("orderId")).longValue();
        Long userId = ((Number) eventData.get("userId")).longValue();

        createNotification(
                userId,
                NotificationType.ORDER_STATUS,
                "[주문 조리 시작]",
                String.format("주문이 조리 시작되었습니다.\n주문 번호: %d", orderId));
    }

    @Transactional
    private void processOrderReadyEvent(Map<String, Object> eventData) {
        log.info("Processing order ready event: {}", eventData);

        Long orderId = ((Number) eventData.get("orderId")).longValue();
        Long userId = ((Number) eventData.get("userId")).longValue();

        createNotification(
                userId,
                NotificationType.ORDER_STATUS,
                "[주문 준비 완료]",
                String.format("주문이 준비 완료되었습니다.\n주문 번호: %d", orderId));
    }

    @Transactional
    private void processOrderPickedUpEvent(Map<String, Object> eventData) {
        log.info("Processing order picked up event: {}", eventData);

        Long orderId = ((Number) eventData.get("orderId")).longValue();
        Long userId = ((Number) eventData.get("userId")).longValue();

        createNotification(
                userId,
                NotificationType.ORDER_STATUS,
                "[주문 배달 픽업]",
                String.format("주문이 픽업되었습니다.\n주문 번호: %d", orderId));
    }

    @Transactional
    private void processOrderDeliveredEvent(Map<String, Object> eventData) {
        log.info("Processing order delivered event: {}", eventData);

        Long orderId = ((Number) eventData.get("orderId")).longValue();
        Long userId = ((Number) eventData.get("userId")).longValue();

        createNotification(
                userId,
                NotificationType.ORDER_STATUS,
                "[주문 배달 완료]",
                String.format("주문이 배달 완료되었습니다.\n주문 번호: %d", orderId));
    }

    @Transactional
    private void processOrderCancelledEvent(Map<String, Object> eventData) {
        log.info("Processing order cancelled event: {}", eventData);

        Long orderId = ((Number) eventData.get("orderId")).longValue();
        Long userId = ((Number) eventData.get("userId")).longValue();

        Map<String, Object> order = (Map<String, Object>) eventData.get("order");
        String cancelReason = (String) order.get("cancelReason");

        createNotification(
                userId,
                NotificationType.ORDER_STATUS,
                "[주문 취소]",
                String.format("주문이 취소되었습니다.\n주문 번호: %d\n취소 사유: %s", orderId, cancelReason));
    }

    @Transactional
    private void processOrderRejectedEvent(Map<String, Object> eventData) {
        log.info("Processing order rejected event: {}", eventData);

        Long orderId = ((Number) eventData.get("orderId")).longValue();
        Long userId = ((Number) eventData.get("userId")).longValue();

        Map<String, Object> order = (Map<String, Object>) eventData.get("order");
        String rejectReason = (String) order.get("rejectReason");

        createNotification(
                userId,
                NotificationType.ORDER_STATUS,
                "[주문 거절]",
                String.format("주문이 거절되었습니다.\n주문 번호: %d\n거절 사유: %s", orderId, rejectReason));
    }

    private void createNotification(Long userId, NotificationType type, String title, String content) {
        // 1. 데이터베이스에 알림 저장
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType(type);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setIsRead(false);

        notificationRepository.save(notification);
        log.info("Notification created for user {}: {}", userId, title);

        // 2. SSE로 실시간 알림 전송
        sseService.sendCustomerNotificationToUser(userId, String.format("%s\n%s", title, content));
    }

}
