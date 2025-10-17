package io.goorm.team02.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 * 테스트 이벤트 응답 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestEventResponse {
    private boolean success;
    private String eventId;
    private String message;
    private String error;
}


