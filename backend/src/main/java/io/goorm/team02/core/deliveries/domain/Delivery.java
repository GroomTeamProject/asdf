package io.goorm.team02.core.deliveries.domain;

import io.goorm.team02.core.common.domain.BaseEntity;
import io.goorm.team02.core.deliveries.domain.enums.DeliveryStatus;
import io.goorm.team02.core.orders.domain.Order;
import io.goorm.team02.core.users.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
@Table(name = "deliveries")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Delivery extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rider_id")
    private User rider;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String pickupAddress;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String deliveryAddress;

    @Column(precision = 5, scale = 2)
    private BigDecimal distanceKm;

    private Integer estimatedTime;

    @Enumerated(EnumType.STRING)
    //@Column(nullable = false)
    @Setter
    private DeliveryStatus status;// = DeliveryStatus.REQUESTED;

    @Setter private LocalDateTime acceptedAt;
    @Setter private LocalDateTime pickedUpAt;
    @Setter private LocalDateTime deliveredAt;

    @OneToMany(mappedBy = "delivery", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DeliveryLocation> locations = new ArrayList<>();

    private LocalDateTime requestedAt;

    @Setter
    @Column(precision = 10, scale = 2)
    private BigDecimal deliveryFee = BigDecimal.ZERO;

    @PrePersist
    private void onCreate() {
        /*
        if (requestedAt == null) requestedAt = LocalDateTime.now();
        if (status == null) status = DeliveryStatus.REQUESTED;
        if (deliveryFee == null) deliveryFee = BigDecimal.ZERO;
         */
    }

    public static Delivery accept(Order order, User rider) {
        Delivery d = new Delivery();
        d.order = order;
        d.rider = rider;
        d.status = DeliveryStatus.ACCEPTED;
        d.acceptedAt = LocalDateTime.now();
        d.pickupAddress   = join(order.getStoreAddress(),    order.getStoreDetailAddress());
        d.deliveryAddress = join(order.getDeliveryAddress(), order.getDeliveryDetailAddress());
        d.deliveryFee = order.getDeliveryFee() != null ? order.getDeliveryFee() : BigDecimal.ZERO;
        return d;
    }

    private static String join(String a, String b) {
        return Stream.of(a, b).filter(Objects::nonNull).collect(Collectors.joining(" "));
    }

    public void pickup() {
        if(this.status == DeliveryStatus.ACCEPTED){
            this.status = DeliveryStatus.PICKED_UP;
            this.pickedUpAt = LocalDateTime.now();

        }else{
            throw new IllegalArgumentException("Delivery status is not accepted");
        }
    }

    public void complete() {
        if(this.status == DeliveryStatus.PICKED_UP){
            this.status = DeliveryStatus.DELIVERED;
            this.deliveredAt = LocalDateTime.now();

        }else{
            throw  new IllegalArgumentException("Delivery status is not picked up");
        }
    }
}
