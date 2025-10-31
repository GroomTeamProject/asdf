package io.goorm.team02.order.repository;

import io.goorm.team02.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    // 특정 주문에 속한 OrderItem 조회
    List<OrderItem> findByOrderId(Long orderId);
}
