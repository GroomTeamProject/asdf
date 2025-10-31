package io.goorm.team02.dto.deliveries;


import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "배달 기록 조회 요청")
public record DeliveryResponse(
        Long id,
        Long orderId,
        Long riderId,
        String pickupAddress,
        String completeAddress,
        Integer estimatedTime,
        BigDecimal distanceKm,
        LocalDateTime acceptedAt,
        LocalDateTime pickedUpAt,
        LocalDateTime deliveredAt,
        BigDecimal deliveryFee // <- BigDecimal, 마지막에 콤마 없음
){

}