package io.goorm.team02.core.orders.repository;

import io.goorm.team02.core.orders.domain.Order;
import io.goorm.team02.core.orders.domain.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o " +
           "LEFT JOIN FETCH o.user u " +
           "LEFT JOIN FETCH o.store s " +
           "WHERE o.store.id = :storeId " +
           "ORDER BY o.orderedAt DESC")
    List<Order> findAllByStoreIdWithDetails(@Param("storeId") Long storeId);

    List<Order> findAllByStoreId(Long storeId);

    /**
     * 특정 상태의 주문들 조회 (배달 가능한 주문들)
     */
    @Query("SELECT o FROM Order o " +
            "LEFT JOIN FETCH o.user u " +
            "LEFT JOIN FETCH o.store s " +
            "WHERE o.status = :status " +
            "ORDER BY o.cookingCompletedAt ASC")
    List<Order> findByStatus(@Param("status") OrderStatus status);

    /**
     * 특정 가게의 특정 상태 주문들 조회
     */
    @Query("SELECT o FROM Order o " +
            "LEFT JOIN FETCH o.user u " +
            "LEFT JOIN FETCH o.store s " +
            "WHERE o.store.id = :storeId AND o.status = :status " +
            "ORDER BY o.cookingCompletedAt ASC")
    List<Order> findByStoreIdAndStatus(@Param("storeId") Long storeId, @Param("status") OrderStatus status);
}
