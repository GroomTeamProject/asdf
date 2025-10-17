package io.goorm.team02.demo.service;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;
import io.goorm.team02.demo.client.OrderServiceClient;
import io.goorm.team02.demo.dto.OrderResponse;
import io.goorm.team02.demo.dto.TestEventResponse;
import io.goorm.team02.demo.event.TestEvent;
import io.goorm.team02.kafka.client.EventPublisher;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class DemoService {

    private final OrderServiceClient orderServiceClient;
    private final EventPublisher eventPublisher;

    /**
     * 주문 조회
     */
    public OrderResponse getOrderById(Long id) {
        return orderServiceClient.getOrderById(id);
    }

    /**
     * 이벤트 조회
     */
    public TestEventResponse getEvents() {
        return TestEventResponse.builder()
                .success(true)
                .message("Events retrieved successfully")
                .build();
    }

    /**
     * 비동기 이벤트 발행
     */
    public TestEventResponse publishEvent() {
        try {
            TestEvent event = new TestEvent("event-test", null);
            eventPublisher.publish("test-events", event);

            return TestEventResponse.builder()
                    .success(true)
                    .eventId(event.getEventId())
                    .message("Event published successfully")
                    .build();

        } catch (Exception e) {
            log.error("Failed to publish test event", e);
            return TestEventResponse.builder()
                    .success(false)
                    .error(e.getMessage())
                    .build();
        }
    }

    /**
     * 동기 이벤트 발행
     */
    public TestEventResponse publishEventSync() {
        try {
            TestEvent event = new TestEvent("sync-event-test", null);
            eventPublisher.publishSync("test-events", event);

            return TestEventResponse.builder()
                    .success(true)
                    .eventId(event.getEventId())
                    .message("Event published synchronously")
                    .build();

        } catch (Exception e) {
            log.error("Failed to publish sync test event", e);
            return TestEventResponse.builder()
                    .success(false)
                    .error(e.getMessage())
                    .build();
        }
    }

}
