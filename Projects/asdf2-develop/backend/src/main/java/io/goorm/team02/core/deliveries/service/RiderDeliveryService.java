package io.goorm.team02.core.deliveries.service;

import io.goorm.team02.core.deliveries.domain.Delivery;
import io.goorm.team02.core.deliveries.domain.enums.DeliveryStatus;
import io.goorm.team02.core.deliveries.repository.DeliveryRepository;
import io.goorm.team02.core.orders.domain.enums.OrderStatus;
import io.goorm.team02.core.orders.repository.OrderRepository;
import io.goorm.team02.core.users.repository.UserinfoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class RiderDeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final UserinfoRepository userInfoRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public Delivery accept(Long orderId, Long riderId) {
        var order = orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("Order not found" + orderId));
        var rider = userInfoRepository.findById(riderId).orElseThrow(() -> new IllegalArgumentException("Rider not found" + riderId));

        // 주문 상태 검증 (예: READY 상태에서만 ACCEPT 가능)
        if (order.getStatus() != OrderStatus.READY) {
            throw new IllegalStateException("잘못된 요청입니다. orderId=" + orderId);
        }

        // 이미 Delivery가 있는지 확인
        if (deliveryRepository.findByOrderId(orderId) != null) {
            throw new IllegalStateException("이미 해당 주문에 대한 Delivery가 존재합니다. orderId=" + orderId);
        }
        Delivery delivery = Delivery.accept(order, rider);
        delivery.getOrder().setStatus(OrderStatus.ACCEPTED);
        return deliveryRepository.save(delivery);
    }

    @Transactional
    public Delivery pickup(Long riderId) {
        Delivery delivery = deliveryRepository.findByRiderIdAndStatus(riderId, DeliveryStatus.ACCEPTED)
                .orElseThrow(() -> new NoSuchElementException("이 라이더에게 할당된 배달이 없습니다. riderId=" + riderId));

        delivery.pickup();
        delivery.getOrder().setStatus(OrderStatus.PICKED_UP);
        return delivery;
    }
    @Transactional
    public Delivery complete(Long riderId) {
        Delivery delivery = deliveryRepository.findByRiderIdAndStatus(riderId, DeliveryStatus.PICKED_UP)
                .orElseThrow(() -> new NoSuchElementException("픽업된 배달이 없습니다. riderId=" + riderId));

        delivery.complete();
        delivery.getOrder().setStatus(OrderStatus.DELIVERED);
        return delivery;
    }
}
