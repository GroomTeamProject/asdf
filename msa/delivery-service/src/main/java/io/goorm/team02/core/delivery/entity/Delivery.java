package io.goorm.team02.core.delivery.entity;


import io.goorm.team02.core.delivery.entity.enums.DeliveryStatus;
import io.goorm.team02.dto.orders.OrderResponseForDelivery;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
@Table(name = "deliveries")
@Getter
@Setter
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "order_id", nullable = false)
    private Long orderId;
    @Column(name = "rider_id")
    private Long riderId;
    @Column(name = "pickup_address", nullable = false, columnDefinition = "TEXT")
    private String pickupAddress;
    @Column(name = "delivery_address", nullable = false, columnDefinition = "TEXT")
    private String deliveryAddress;
    @Column(name = "distance_km", precision = 5, scale = 2)
    private BigDecimal distanceKm;
    @Column(name = "estimated_time")
    private Integer estimatedTime;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private DeliveryStatus status;
    @Column(name = "requested_at")
    private LocalDateTime requestedAt;
    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;
    @Column(name = "picked_up_at")
    private LocalDateTime pickedUpAt;
    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @Column(name = "delivery_fee", precision = 10, scale = 2)
    private BigDecimal deliveryFee;

    private static String join(String a, String b) {
        return Stream.of(a, b).filter(Objects::nonNull).collect(Collectors.joining(" "));
    }

    public static Delivery accept(OrderResponseForDelivery order, Long riderId) {
        Delivery d = new Delivery();
        d.orderId = order.id();
        d.riderId = riderId;
        d.status = DeliveryStatus.ACCEPTED;
        d.acceptedAt = LocalDateTime.now();
        d.pickupAddress = join(order.storeAddress(),order.storeDetailAddress());
        d.deliveryAddress = join(order.deliveryAddress(),order.deliveryDetailAddress());
        d.deliveryFee = BigDecimal.valueOf(9999);
        return d;
    }
}