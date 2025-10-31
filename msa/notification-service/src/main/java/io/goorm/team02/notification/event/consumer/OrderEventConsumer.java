package io.goorm.team02.notification.event.consumer;

import io.goorm.team02.kafka.client.BaseEventSubscriber;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import io.goorm.team02.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;

/**
 * 주문 이벤트 Consumer
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class OrderEventConsumer extends BaseEventSubscriber {

    private final NotificationService notificationService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "order-events", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeOrderEvent(@Payload String message) {
        log.info("Received order event: {}", message);

        try {
            // JSON 문자열을 Map으로 파싱
            Map<String, Object> eventData = objectMapper.readValue(message, Map.class);
            
            // Map에서 eventType 추출
            String eventType = (String) eventData.get("eventType");
            notificationService.handleOrderEvent(eventType, eventData);
        } catch (Exception e) {
            log.error("Failed to process order event: {}", message, e);
        }
    }
}
