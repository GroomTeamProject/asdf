package io.goorm.team02.order.event.consumer;

import io.goorm.team02.kafka.client.BaseEventSubscriber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * 테스트 이벤트 Consumer
 */
@Slf4j
@Component
public class TestEventConsumer extends BaseEventSubscriber {

    @KafkaListener(topics = "test-events", groupId = "${spring.kafka.consumer.group-id}")
    public void handleTestEvent(String message) {
        log.info("Received test event: {}", message);

        try {
            // 이벤트 처리 로직
            processTestEvent(message);
        } catch (Exception e) {
            log.error("Failed to process test event: {}", message, e);
        }
    }

    private void processTestEvent(String message) {
        log.info("Processing test event: {}", message);
        // 여기에 실제 비즈니스 로직을 구현
    }
}
