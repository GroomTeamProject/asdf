package io.goorm.team02.kafka.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.goorm.team02.kafka.model.BaseEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * 이벤트 발행 클라이언트
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    /**
     * 이벤트 발행 메서드
     * @param topic 토픽명
     * @param event 발행할 이벤트
     */
    public void publish(String topic, BaseEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            CompletableFuture<Void> future = kafkaTemplate.send(topic, event.getEventId(), message)
                .thenAccept(result -> {
                    log.info("Event published successfully: topic={}, eventId={}, eventType={}, offset={}", 
                        topic, event.getEventId(), event.getEventType(), 
                        result.getRecordMetadata().offset());
                });
            
            future.exceptionally(throwable -> {
                log.error("Failed to publish event: topic={}, eventId={}, error={}", 
                    topic, event.getEventId(), throwable.getMessage());
                return null;
            });
            
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize event: eventId={}, error={}", 
                event.getEventId(), e.getMessage());
        }
    }

    /**
     * 동기 이벤트 발행 (블로킹)
     * @param topic 토픽명
     * @param event 발행할 이벤트
     */
    public void publishSync(String topic, BaseEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(topic, event.getEventId(), message).get();
            log.info("Event published synchronously: topic={}, eventId={}, eventType={}", 
                topic, event.getEventId(), event.getEventType());
        } catch (Exception e) {
            log.error("Failed to publish event synchronously: topic={}, eventId={}, error={}", 
                topic, event.getEventId(), e.getMessage());
        }
    }
}
