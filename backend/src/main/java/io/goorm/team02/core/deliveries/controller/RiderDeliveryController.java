package io.goorm.team02.core.deliveries.controller;


import io.goorm.team02.core.deliveries.controller.dto.AcceptRequest;
import io.goorm.team02.core.deliveries.controller.dto.ApiResponse;
import io.goorm.team02.core.deliveries.controller.dto.DeliveryResponse;
import io.goorm.team02.core.deliveries.domain.Delivery;
import io.goorm.team02.core.deliveries.domain.enums.DeliveryStatus;
import io.goorm.team02.core.deliveries.repository.DeliveryRepository;
import io.goorm.team02.core.deliveries.service.RiderDeliveryHistoryService;
import io.goorm.team02.core.deliveries.service.RiderDeliveryService;
import io.goorm.team02.core.deliveries.service.RiderQueryService;
import io.goorm.team02.core.orders.domain.Order;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rider")
public class RiderDeliveryController {
    private final RiderDeliveryService riderDeliveryService;
    private final RiderQueryService riderQueryService;
    private final RiderDeliveryHistoryService riderDeliveryHistoryService;
    private final DeliveryRepository deliveryRepository;

    @PostMapping("/{order_id}/accept")
    public ResponseEntity<ApiResponse<DeliveryResponse>> accept(@PathVariable Long order_id, @RequestBody @Valid AcceptRequest acceptRequest) {
        var d = riderDeliveryService.accept(order_id,acceptRequest.riderId());
        return ResponseEntity.status(201).body(ApiResponse.ok(DeliveryResponse.of(d)));
    }

    @PutMapping("/{order_id}/pickup")
    public ResponseEntity<ApiResponse<DeliveryResponse>> pickup(@PathVariable Long order_id){
        var d = riderDeliveryService.pickup(order_id);
        return ResponseEntity.ok(ApiResponse.ok(DeliveryResponse.of(d)));
    }

    @PutMapping("/{order_id}/complete")
    public ResponseEntity<ApiResponse<DeliveryResponse>> complete(@PathVariable Long order_id){
        var d = riderDeliveryService.complete(order_id);
        return ResponseEntity.ok(ApiResponse.ok(DeliveryResponse.of(d)));
    }

    @GetMapping("/{rider_id}/today-count")
    public ResponseEntity<Long> getTodayCount(@PathVariable Long rider_id) {
        Long count = riderQueryService.getTodayCount(rider_id);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/{rider_id}/today-income")
    public ResponseEntity<Long> getTodayIncome(@PathVariable Long rider_id) {
        Long income =  riderQueryService.getTodayIncome(rider_id);
        return ResponseEntity.ok(income);
    }

    @GetMapping("/{rider_id}/today-avg")
    public ResponseEntity<Long> getTodayAvgMinutes(@PathVariable Long rider_id) {
        Long avg =  riderQueryService.getTodayAvgMinutes(rider_id);
        return ResponseEntity.ok(avg);
    }

    @GetMapping("/{rider_id}/currentDelivery")
    public ResponseEntity<DeliveryResponse> getCurrentDelivery(@PathVariable Long rider_id) {
        Delivery d =  riderQueryService.getCurrentDelivery(rider_id);
        return ResponseEntity.ok(d == null ? null : DeliveryResponse.of(d));
    }

    @GetMapping("/{rider_id}/status")
    public ResponseEntity<DeliveryStatus> getStatus(@PathVariable Long rider_id) {
        Delivery currentDelivery = riderQueryService.getCurrentDelivery(rider_id);
        return ResponseEntity.ok(currentDelivery != null ? currentDelivery.getStatus() : null); // 200 + null
    }

    @GetMapping("/{rider_id}/history")
    public ResponseEntity<List<DeliveryResponse>> getHistory(@PathVariable("rider_id") Long riderId) {
        return ResponseEntity.ok(riderDeliveryHistoryService.getDeliveries(riderId));
    }
}
