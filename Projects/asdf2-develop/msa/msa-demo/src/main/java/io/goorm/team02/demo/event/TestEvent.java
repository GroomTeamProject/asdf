package io.goorm.team02.demo.event;

import io.goorm.team02.kafka.model.BaseEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 테스트 이벤트
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class TestEvent extends BaseEvent {
    private String message;
    private Map<String, Object> data;

    public TestEvent(String message, Map<String, Object> data) {
        super("TEST_EVENT", "demo-service");
        this.message = message;
        this.data = data;
    }
}
