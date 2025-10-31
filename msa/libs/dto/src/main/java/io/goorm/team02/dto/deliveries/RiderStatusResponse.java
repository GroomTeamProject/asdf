package io.goorm.team02.dto.deliveries;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema (description = "라이더의 상태 요청 응답")
public record RiderStatusResponse(Long riderId, String status) {
}