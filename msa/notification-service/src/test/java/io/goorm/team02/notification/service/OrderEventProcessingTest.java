package io.goorm.team02.notification.service;

import io.goorm.team02.notification.entity.Notification;
import io.goorm.team02.notification.entity.enums.NotificationType;
import io.goorm.team02.notification.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("주문 이벤트 처리 테스트")
class OrderEventProcessingTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private SSEService sseService;

    @InjectMocks
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        // Mock 설정은 각 테스트에서 필요할 때만 설정
    }

    @Test
    @DisplayName("ORDER_CREATED 이벤트 처리")
    void processOrderCreatedEvent() {
        // given
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("orderId", 123L);
        eventData.put("userId", 1L);

        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> {
            Notification notification = invocation.getArgument(0);
            notification.setId(1L);
            return notification;
        });

        // when
        notificationService.handleOrderEvent("ORDER_CREATED", eventData);

        // then
        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(notificationCaptor.capture());
        
        Notification savedNotification = notificationCaptor.getValue();
        assertThat(savedNotification.getUserId()).isEqualTo(1L);
        assertThat(savedNotification.getType()).isEqualTo(NotificationType.ORDER_STATUS);
        assertThat(savedNotification.getTitle()).isEqualTo("[주문 접수]");
        assertThat(savedNotification.getContent()).contains("주문이 접수되었습니다");
        assertThat(savedNotification.getContent()).contains("주문 번호: 123");
        assertThat(savedNotification.getIsRead()).isFalse();

        // SSE 알림 전송 확인
        verify(sseService).sendCustomerNotificationToUser(eq(1L), anyString());
    }

    @Test
    @DisplayName("ORDER_ACCEPTED 이벤트 처리")
    void processOrderAcceptedEvent() {
        // given
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("orderId", 123L);
        eventData.put("userId", 1L);
        eventData.put("minCookingTime", 30);
        eventData.put("maxCookingTime", 45);

        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> {
            Notification notification = invocation.getArgument(0);
            notification.setId(1L);
            return notification;
        });

        // when
        notificationService.handleOrderEvent("ORDER_ACCEPTED", eventData);

        // then
        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(notificationCaptor.capture());
        
        Notification savedNotification = notificationCaptor.getValue();
        assertThat(savedNotification.getUserId()).isEqualTo(1L);
        assertThat(savedNotification.getType()).isEqualTo(NotificationType.ORDER_STATUS);
        assertThat(savedNotification.getTitle()).isEqualTo("[주문 수락]");
        assertThat(savedNotification.getContent()).contains("주문이 수락되었습니다");
        assertThat(savedNotification.getContent()).contains("주문 번호: 123");
        assertThat(savedNotification.getContent()).contains("예상 조리 시간: 30 ~ 45분");
    }

    @Test
    @DisplayName("ORDER_CANCELLED 이벤트 처리")
    void processOrderCancelledEvent() {
        // given
        Map<String, Object> order = new HashMap<>();
        order.put("cancelReason", "재료 부족");
        
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("orderId", 123L);
        eventData.put("userId", 1L);
        eventData.put("order", order);

        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> {
            Notification notification = invocation.getArgument(0);
            notification.setId(1L);
            return notification;
        });

        // when
        notificationService.handleOrderEvent("ORDER_CANCELLED", eventData);

        // then
        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(notificationCaptor.capture());
        
        Notification savedNotification = notificationCaptor.getValue();
        assertThat(savedNotification.getUserId()).isEqualTo(1L);
        assertThat(savedNotification.getType()).isEqualTo(NotificationType.ORDER_STATUS);
        assertThat(savedNotification.getTitle()).isEqualTo("[주문 취소]");
        assertThat(savedNotification.getContent()).contains("주문이 취소되었습니다");
        assertThat(savedNotification.getContent()).contains("주문 번호: 123");
        assertThat(savedNotification.getContent()).contains("취소 사유: 재료 부족");
    }

    @Test
    @DisplayName("ORDER_REJECTED 이벤트 처리")
    void processOrderRejectedEvent() {
        // given
        Map<String, Object> order = new HashMap<>();
        order.put("rejectReason", "재료 부족으로 인한 주문 거절");
        
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("orderId", 123L);
        eventData.put("userId", 1L);
        eventData.put("order", order);

        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> {
            Notification notification = invocation.getArgument(0);
            notification.setId(1L);
            return notification;
        });

        // when
        notificationService.handleOrderEvent("ORDER_REJECTED", eventData);

        // then
        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(notificationCaptor.capture());
        
        Notification savedNotification = notificationCaptor.getValue();
        assertThat(savedNotification.getUserId()).isEqualTo(1L);
        assertThat(savedNotification.getType()).isEqualTo(NotificationType.ORDER_STATUS);
        assertThat(savedNotification.getTitle()).isEqualTo("[주문 거절]");
        assertThat(savedNotification.getContent()).contains("주문이 거절되었습니다");
        assertThat(savedNotification.getContent()).contains("주문 번호: 123");
        assertThat(savedNotification.getContent()).contains("거절 사유: 재료 부족으로 인한 주문 거절");
    }

    @Test
    @DisplayName("알 수 없는 이벤트 타입 처리")
    void handleUnknownEventType() {
        // given
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("orderId", 123L);
        eventData.put("userId", 1L);

        // when
        notificationService.handleOrderEvent("UNKNOWN_EVENT", eventData);

        // then
        verify(notificationRepository, never()).save(any(Notification.class));
        verify(sseService, never()).sendCustomerNotificationToUser(anyLong(), anyString());
    }
}
