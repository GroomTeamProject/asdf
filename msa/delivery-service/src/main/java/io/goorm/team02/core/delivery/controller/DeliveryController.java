package io.goorm.team02.core.delivery.controller;

import io.goorm.team02.core.delivery.controller.dto.ApiResponse;
import io.goorm.team02.core.delivery.entity.Delivery;
import io.goorm.team02.core.delivery.mapper.DeliveryMapper;
import io.goorm.team02.core.delivery.service.DeliveryService;
import io.goorm.team02.dto.deliveries.DeliveryResponse;
import io.goorm.team02.security.annotation.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;
    private final DeliveryMapper deliveryMapper;

    @PostMapping("/{orderId}/accept")
    public ResponseEntity<ApiResponse<DeliveryResponse>> accept(
            @PathVariable Long orderId,
            @CurrentUser Long riderId
    ) {
        Delivery delivery = deliveryService.accept(riderId, orderId);
        DeliveryResponse response = deliveryMapper.toResponse(delivery);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(response));
    }

    @PutMapping("/{orderId}/pickup")
    public ResponseEntity<ApiResponse<DeliveryResponse>> pickup(
            @PathVariable Long orderId,
            @CurrentUser Long riderId
    ) {
        Delivery delivery = deliveryService.pickup(riderId, orderId);
        DeliveryResponse response = deliveryMapper.toResponse(delivery);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/{orderId}/complete")
    public ResponseEntity<ApiResponse<DeliveryResponse>> complete(
            @PathVariable Long orderId,
            @CurrentUser Long riderId
    ) {
        Delivery delivery = deliveryService.complete(riderId, orderId);
        DeliveryResponse response = deliveryMapper.toResponse(delivery);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<?>> history(@CurrentUser Long riderId) {
        List<DeliveryResponse> deliveries = deliveryService.getDeliveries(riderId);
        if (deliveries.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.fail("[history]: 조회 결과가 없습니다."));
        }
        return ResponseEntity.ok(ApiResponse.ok(deliveries));
    }

    @GetMapping("/current")
    public ResponseEntity<ApiResponse<?>> currentDelivery(@CurrentUser Long riderId) {
        Optional<Delivery> deliveryOpt = deliveryService.getCurrentDelivery(riderId);
        if (deliveryOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.fail("[currentDelivery]: 현재 진행 중인 배달이 없습니다."));
        }

        DeliveryResponse response = deliveryMapper.toResponse(deliveryOpt.get());
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/today-count")
    public ResponseEntity<ApiResponse<BigDecimal>> todayCount(@CurrentUser Long riderId) {
        return ResponseEntity.ok(ApiResponse.ok(deliveryService.getTodayCount(riderId)));
    }

    @GetMapping("/today-income")
    public ResponseEntity<ApiResponse<BigDecimal>> todayIncome(@CurrentUser Long riderId) {
        return ResponseEntity.ok(ApiResponse.ok(deliveryService.getTodayIncome(riderId)));
    }

    @GetMapping("/today-avg")
    public ResponseEntity<ApiResponse<Long>> todayAvg(@CurrentUser Long riderId) {
        return ResponseEntity.ok(ApiResponse.ok(deliveryService.getTodayAvg(riderId)));
    }

    @GetMapping("/rider-status")
    public ResponseEntity<ApiResponse<String>> riderStatus(@CurrentUser Long riderId) {
        return ResponseEntity.ok(ApiResponse.ok(deliveryService.getRiderStatus(riderId)));
    }
}
