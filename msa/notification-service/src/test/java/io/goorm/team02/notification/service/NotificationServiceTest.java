package io.goorm.team02.notification.service;

import io.goorm.team02.notification.dto.NotificationStats;
import io.goorm.team02.notification.entity.Notification;
import io.goorm.team02.notification.entity.enums.NotificationType;
import io.goorm.team02.notification.repository.NotificationRepository;
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

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationService 테스트")
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private SSEService sseService;

    @InjectMocks
    private NotificationService notificationService;

    private Notification notification1;
    private Notification notification2;
    private Notification notification3;

    @BeforeEach
    void setUp() {
        notification1 = createNotification(1L, 1L, "알림1", false);
        notification2 = createNotification(2L, 1L, "알림2", true);
        notification3 = createNotification(3L, 1L, "알림3", false);
    }

    @Test
    @DisplayName("getUnreadCount - 읽지 않은 알림 개수 조회")
    void getUnreadCount() {
        // given
        when(notificationRepository.countByUserIdAndIsReadFalse(1L)).thenReturn(2L);

        // when
        long unreadCount = notificationService.getUnreadCount(1L);

        // then
        assertThat(unreadCount).isEqualTo(2L);
        verify(notificationRepository).countByUserIdAndIsReadFalse(1L);
    }

    @Test
    @DisplayName("getUnreadNotifications - 페이징 조회")
    void getUnreadNotifications_withPageable() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        List<Notification> unreadNotifications = Arrays.asList(notification1, notification3);
        Page<Notification> page = new PageImpl<>(unreadNotifications, pageable, 2);
        when(notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(1L, pageable))
                .thenReturn(page);

        // when
        Page<Notification> result = notificationService.getUnreadNotifications(1L, pageable);

        // then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).containsExactly(notification1, notification3);
        assertThat(result.getTotalElements()).isEqualTo(2);
        verify(notificationRepository).findByUserIdAndIsReadFalseOrderByCreatedAtDesc(1L, pageable);
    }

    @Test
    @DisplayName("markAsRead - 특정 알림을 읽음 상태로 변경 성공")
    void markAsRead_success() {
        // given
        when(notificationRepository.markAsRead(1L, 1L)).thenReturn(1);

        // when
        notificationService.markAsRead(1L, 1L);

        // then
        verify(notificationRepository).markAsRead(1L, 1L);
    }

    @Test
    @DisplayName("markAllAsRead - 모든 알림을 읽음 상태로 변경")
    void markAllAsRead() {
        // given
        when(notificationRepository.markAllAsRead(1L)).thenReturn(2);

        // when
        notificationService.markAllAsRead(1L);

        // then
        verify(notificationRepository).markAllAsRead(1L);
    }

    @Test
    @DisplayName("getNotificationStats - 알림 통계 정보 조회")
    void getNotificationStats() {
        // given
        when(notificationRepository.countByUserId(1L)).thenReturn(3L);
        when(notificationRepository.countByUserIdAndIsReadFalse(1L)).thenReturn(2L);

        // when
        NotificationStats stats = notificationService.getNotificationStats(1L);

        // then
        assertThat(stats.totalCount()).isEqualTo(3L);
        assertThat(stats.readCount()).isEqualTo(1L);
        assertThat(stats.unreadCount()).isEqualTo(2L);
        verify(notificationRepository).countByUserId(1L);
        verify(notificationRepository).countByUserIdAndIsReadFalse(1L);
    }

    @Test
    @DisplayName("getUserNotifications - 사용자 알림 페이징 조회")
    void getUserNotifications_withPageable() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        List<Notification> allNotifications = Arrays.asList(notification1, notification2, notification3);
        Page<Notification> page = new PageImpl<>(allNotifications, pageable, 3);
        when(notificationRepository.findByUserIdOrderByCreatedAtDesc(1L, pageable))
                .thenReturn(page);

        // when
        Page<Notification> result = notificationService.getUserNotifications(1L, pageable);

        // then
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getContent()).containsExactly(notification1, notification2, notification3);
        assertThat(result.getTotalElements()).isEqualTo(3);
        verify(notificationRepository).findByUserIdOrderByCreatedAtDesc(1L, pageable);
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
