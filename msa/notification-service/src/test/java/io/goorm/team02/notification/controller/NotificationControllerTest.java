package io.goorm.team02.notification.controller;

import io.goorm.team02.notification.dto.NotificationResponse;
import io.goorm.team02.notification.dto.NotificationStats;
import io.goorm.team02.notification.entity.Notification;
import io.goorm.team02.notification.entity.enums.NotificationType;
import io.goorm.team02.notification.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationController 테스트")
class NotificationControllerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationController notificationController;

    private Notification notification1;
    private Notification notification2;

    @BeforeEach
    void setUp() {
        notification1 = createNotification(1L, 1L, "알림1", false);
        notification2 = createNotification(2L, 1L, "알림2", true);
    }

    @Test
    @DisplayName("getUnreadCount - 읽지 않은 알림 개수 조회")
    void getUnreadCount() {
        // given
        when(notificationService.getUnreadCount(1L)).thenReturn(3L);

        // when
        ResponseEntity<Map<String, Long>> response = notificationController.getUnreadCount(1L);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsEntry("unreadCount", 3L);
        verify(notificationService).getUnreadCount(1L);
    }

    @Test
    @DisplayName("getUserNotificationsPage - 사용자 알림 페이징 조회")
    void getUserNotificationsPage() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        List<Notification> notifications = Arrays.asList(notification1, notification2);
        Page<Notification> page = new PageImpl<>(notifications, pageable, 2);
        when(notificationService.getUserNotifications(1L, pageable)).thenReturn(page);

        // when
        ResponseEntity<Page<NotificationResponse>> response = 
                notificationController.getUserNotificationsPage(1L, pageable);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getContent()).hasSize(2);
        verify(notificationService).getUserNotifications(1L, pageable);
    }

    @Test
    @DisplayName("getUnreadNotificationsPage - 읽지 않은 알림 페이징 조회")
    void getUnreadNotificationsPage() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        List<Notification> unreadNotifications = Arrays.asList(notification1);
        Page<Notification> page = new PageImpl<>(unreadNotifications, pageable, 1);
        when(notificationService.getUnreadNotifications(1L, pageable)).thenReturn(page);

        // when
        ResponseEntity<Page<NotificationResponse>> response = 
                notificationController.getUnreadNotificationsPage(1L, pageable);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getContent()).hasSize(1);
        verify(notificationService).getUnreadNotifications(1L, pageable);
    }

    @Test
    @DisplayName("markAsRead - 특정 알림을 읽음 상태로 변경")
    void markAsRead() {
        // given
        doNothing().when(notificationService).markAsRead(1L, 1L);

        // when
        ResponseEntity<Map<String, Object>> response = 
                notificationController.markAsRead(1L, 1L);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsEntry("success", true);
        assertThat(response.getBody()).containsEntry("message", "알림을 읽음 상태로 변경했습니다.");
        verify(notificationService).markAsRead(1L, 1L);
    }

    @Test
    @DisplayName("markAllAsRead - 모든 알림을 읽음 상태로 변경")
    void markAllAsRead() {
        // given
        doNothing().when(notificationService).markAllAsRead(1L);

        // when
        ResponseEntity<Map<String, Object>> response = 
                notificationController.markAllAsRead(1L);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsEntry("success", true);
        assertThat(response.getBody()).containsEntry("message", "모든 알림을 읽음 상태로 변경했습니다.");
        verify(notificationService).markAllAsRead(1L);
    }

    @Test
    @DisplayName("getNotificationStats - 알림 통계 정보 조회")
    void getNotificationStats() {
        // given
        NotificationStats stats = new NotificationStats(10L, 7L, 3L);
        when(notificationService.getNotificationStats(1L)).thenReturn(stats);

        // when
        ResponseEntity<NotificationStats> response = 
                notificationController.getNotificationStats(1L);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(stats);
        assertThat(response.getBody().totalCount()).isEqualTo(10L);
        assertThat(response.getBody().readCount()).isEqualTo(7L);
        assertThat(response.getBody().unreadCount()).isEqualTo(3L);
        verify(notificationService).getNotificationStats(1L);
    }

    private Notification createNotification(Long id, Long userId, String title, boolean isRead) {
        Notification notification = new Notification();
        notification.setId(id);
        notification.setUserId(userId);
        notification.setType(NotificationType.ORDER_STATUS);
        notification.setTitle(title);
        notification.setContent("테스트 내용");
        notification.setIsRead(isRead);
        return notification;
    }
}
