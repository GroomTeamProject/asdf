package io.goorm.team02.kafka.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.goorm.team02.kafka.model.BaseEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.Acknowledgment;

/**
 * 기본 이벤트 구독 클래스
 */
@Slf4j
public abstract class BaseEventSubscriber {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * JSON 메시지를 이벤트 객체로 역직렬화
     * 
     * @param message    JSON 메시지
     * @param eventClass 이벤트 클래스
     * @return 역직렬화된 이벤트 객체
     */
    protected <T extends BaseEvent> T deserialize(String message, Class<T> eventClass) {
        try {
            return objectMapper.readValue(message, eventClass);
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize event: message={}, error={}", message, e.getMessage());
            throw new RuntimeException("Event deserialization failed", e);
        }
    }

    /**
     * 이벤트 처리 성공 시 호출
     * 
     * @param acknowledgment 수동 커밋 객체
     */
    protected void acknowledge(Acknowledgment acknowledgment) {
        try {
            acknowledgment.acknowledge();
            log.debug("Event acknowledged successfully");
        } catch (Exception e) {
            log.error("Failed to acknowledge event: {}", e.getMessage());
        }
    }

    /**
     * 이벤트 처리 실패 시 호출
     * 
     * @param acknowledgment 수동 커밋 객체
     * @param error          에러 정보
     */
    protected void handleError(Acknowledgment acknowledgment, Exception error) {
        log.error("Event processing failed: {}", error.getMessage());
        // 필요에 따라 에러 처리 로직 구현
        // 예: DLQ 전송, 재시도 등
    }

    /**
     * 공통 이벤트 처리 로직
     * 
     * @param message        JSON 메시지
     * @param acknowledgment 수동 커밋 객체
     * @param eventClass     이벤트 클래스
     * @param processor      이벤트 처리 로직
     */
    protected <T extends BaseEvent> void processEvent(
            String message,
            Acknowledgment acknowledgment,
            Class<T> eventClass,
            EventProcessor<T> processor) {
        try {
            T event = deserialize(message, eventClass);
            log.info("Processing event: eventId={}, eventType={}",
                    event.getEventId(), event.getEventType());

            processor.process(event);
            acknowledge(acknowledgment);

        } catch (Exception e) {
            handleError(acknowledgment, e);
        }
    }

    /**
     * 이벤트 처리 함수형 인터페이스
     */
    @FunctionalInterface
    public interface EventProcessor<T extends BaseEvent> {
        void process(T event);
    }
}
