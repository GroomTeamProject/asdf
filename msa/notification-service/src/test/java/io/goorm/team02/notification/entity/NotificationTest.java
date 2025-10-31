package io.goorm.team02.notification.entity;

import io.goorm.team02.notification.entity.enums.NotificationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Notification 엔티티 테스트")
class NotificationTest {

    private Notification notification;

    @BeforeEach
    void setUp() {
        notification = new Notification();
        notification.setUserId(1L);
        notification.setType(NotificationType.ORDER_STATUS);
        notification.setTitle("테스트 알림");
        notification.setContent("테스트 내용");
        notification.setIsRead(false);
    }

    @Test
    @DisplayName("알림 생성 - 기본값 설정 확인")
    void createNotification() {
        // then
        assertThat(notification.getUserId()).isEqualTo(1L);
        assertThat(notification.getType()).isEqualTo(NotificationType.ORDER_STATUS);
        assertThat(notification.getTitle()).isEqualTo("테스트 알림");
        assertThat(notification.getContent()).isEqualTo("테스트 내용");
        assertThat(notification.getIsRead()).isFalse();
    }

    @Test
    @DisplayName("읽음 상태 변경 - false에서 true로")
    void changeReadStatusToTrue() {
        // given
        assertThat(notification.getIsRead()).isFalse();

        // when
        notification.setIsRead(true);

        // then
        assertThat(notification.getIsRead()).isTrue();
    }

    @Test
    @DisplayName("읽음 상태 변경 - true에서 false로")
    void changeReadStatusToFalse() {
        // given
        notification.setIsRead(true);
        assertThat(notification.getIsRead()).isTrue();

        // when
        notification.setIsRead(false);

        // then
        assertThat(notification.getIsRead()).isFalse();
    }

    @Test
    @DisplayName("알림 정보 수정")
    void updateNotificationInfo() {
        // when
        notification.setTitle("수정된 제목");
        notification.setContent("수정된 내용");
        notification.setData("{\"key\": \"value\"}");

        // then
        assertThat(notification.getTitle()).isEqualTo("수정된 제목");
        assertThat(notification.getContent()).isEqualTo("수정된 내용");
        assertThat(notification.getData()).isEqualTo("{\"key\": \"value\"}");
    }

    @Test
    @DisplayName("사용자 ID 변경")
    void changeUserId() {
        // when
        notification.setUserId(2L);

        // then
        assertThat(notification.getUserId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("알림 타입 변경")
    void changeNotificationType() {
        // when
        notification.setType(NotificationType.PROMOTION);

        // then
        assertThat(notification.getType()).isEqualTo(NotificationType.PROMOTION);
    }
}
