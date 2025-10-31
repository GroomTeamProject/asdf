package io.goorm.team02.notification.dto;

/**
 * 알림 통계 정보 DTO
 */
public record NotificationStats(
        long totalCount,    // 전체 알림 개수
        long readCount,     // 읽은 알림 개수
        long unreadCount    // 읽지 않은 알림 개수
) {
}
