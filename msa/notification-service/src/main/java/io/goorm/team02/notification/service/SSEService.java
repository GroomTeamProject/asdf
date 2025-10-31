package io.goorm.team02.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

/**
 * SSE 서비스
 * 실시간 알림을 위한 Server-Sent Events 메시지 전송 기능을 제공합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SSEService {

    // 사용자별 SSE 연결 관리
    private final SSEConnectionService sseConnectionService;

    /**
     * 특정 사용자에게 실시간 알림 전송
     * 
     * @param userId 사용자 ID
     * @param notificationMessage 알림 메시지
     */
    private void sendNotificationToUser(Long userId, String notificationMessage, String notificationType) {
        SseEmitter emitter = sseConnectionService.getConnection(userId);
        
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                    .name(notificationType)
                    .data(notificationMessage));
                log.info("사용자 {}에게 SSE 알림 전송 [{}]: {}", userId, notificationType, notificationMessage);
            } catch (IOException e) {
                log.error("사용자 {}에게 SSE 알림 전송 실패 [{}]: {}", userId, notificationType, e.getMessage());
                sseConnectionService.removeConnection(userId);
            }
        } else {
            log.warn("사용자 {}의 SSE 연결이 없습니다", userId);
        }
    }

    /**
     * 특정 사용자에게 기본 알림 전송
     * 기본 알림 타입 (notification)
     * 
     * @param userId 사용자 ID
     * @param message 알림 메시지
     */
    public void sendNotificationToUser(Long userId, String message) {
        sendNotificationToUser(userId, message,  "notification");
    }

    /**
     * 고객용 알림 전송 (customer-notification 타입)
     * 
     * @param userId 사용자 ID
     * @param message 알림 메시지
     */
    public void sendCustomerNotificationToUser(Long userId, String message) {
        sendNotificationToUser(userId, message, "customer-notification");
    }

}
