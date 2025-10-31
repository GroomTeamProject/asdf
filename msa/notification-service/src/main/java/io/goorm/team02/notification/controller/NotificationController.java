package io.goorm.team02.notification.controller;

import io.goorm.team02.notification.dto.NotificationResponse;
import io.goorm.team02.notification.dto.NotificationStats;
import io.goorm.team02.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController implements NotificationApiDocs {

    private final NotificationService notificationService;

    /**
     * 사용자의 읽지 않은 알림 개수 조회
     */
    @GetMapping("/user/{userId}/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(@PathVariable Long userId) {
        long unreadCount = notificationService.getUnreadCount(userId);
        return ResponseEntity.ok(Map.of("unreadCount", unreadCount));
    }

    /**
     * 사용자의 알림 페이징 조회
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<NotificationResponse>> getUserNotificationsPage(
            @PathVariable Long userId,
            Pageable pageable) {

        Page<NotificationResponse> notifications = notificationService.getUserNotifications(userId, pageable)
                .map(NotificationResponse::from);

        return ResponseEntity.ok(notifications);
    }

    /**
     * 사용자의 읽지 않은 알림 페이징 조회
     */
    @GetMapping("/user/{userId}/unread/page")
    public ResponseEntity<Page<NotificationResponse>> getUnreadNotificationsPage(
            @PathVariable Long userId,
            Pageable pageable) {
        Page<NotificationResponse> notifications = notificationService.getUnreadNotifications(userId, pageable)
                .map(NotificationResponse::from);
        return ResponseEntity.ok(notifications);
    }

    /**
     * 특정 알림을 읽음 상태로 변경
     */
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Map<String, Object>> markAsRead(
            @PathVariable Long notificationId,
            @RequestParam Long userId) {
        notificationService.markAsRead(notificationId, userId);
        return ResponseEntity.ok(Map.of("success", true, "message", "알림을 읽음 상태로 변경했습니다."));
    }

    /**
     * 사용자의 모든 알림을 읽음 상태로 변경
     */
    @PutMapping("/user/{userId}/read-all")
    public ResponseEntity<Map<String, Object>> markAllAsRead(@PathVariable Long userId) {
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok(Map.of("success", true, "message", "모든 알림을 읽음 상태로 변경했습니다."));
    }

    /**
     * 알림 통계 정보 조회
     */
    @GetMapping("/user/{userId}/stats")
    public ResponseEntity<NotificationStats> getNotificationStats(@PathVariable Long userId) {
        NotificationStats stats = notificationService.getNotificationStats(userId);
        return ResponseEntity.ok(stats);
    }
}