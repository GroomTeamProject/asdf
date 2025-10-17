package io.goorm.team02.core.deliveries.controller.dto;

import io.goorm.team02.core.deliveries.domain.Delivery;
import java.math.BigDecimal;
import java.time.LocalDateTime;

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
) {
    public static DeliveryResponse of(Delivery d) {
        return new DeliveryResponse(
                d.getId(),
                d.getOrder().getId(),
                d.getRider() != null ? d.getRider().getId() : null,
                d.getPickupAddress(),
                d.getDeliveryAddress(), // 필드명은 completeAddress지만 값은 deliveryAddress 사용
                d.getEstimatedTime(),
                d.getDistanceKm(),
                d.getAcceptedAt(),
                d.getPickedUpAt(),
                d.getDeliveredAt(),
                d.getDeliveryFee()
        );
    }
}
