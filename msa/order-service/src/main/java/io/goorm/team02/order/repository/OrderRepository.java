package io.goorm.team02.order.repository;

import io.goorm.team02.order.entity.Order;
import io.goorm.team02.order.entity.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o " +
           "WHERE o.storeId = :storeId " +
           "ORDER BY o.orderedAt DESC")
    List<Order> findAllByStoreIdWithDetails(@Param("storeId") Long storeId);

    @Query("SELECT o FROM Order o " +
           "WHERE o.storeId = :storeId " +
           "ORDER BY o.orderedAt DESC")
    List<Order> findAllByStoreId(@Param("storeId") Long storeId);

    @Query("SELECT o FROM Order o " +
           "WHERE o.userId = :userId " +
           "ORDER BY o.orderedAt DESC")
    List<Order> findAllByUserId(@Param("userId") Long userId);

    // 페이지네이션을 지원하는 메서드들 (내림차순 정렬)
    @Query("SELECT o FROM Order o " +
           "WHERE o.storeId = :storeId " +
           "ORDER BY o.orderedAt DESC")
    Page<Order> findAllByStoreIdWithPagination(@Param("storeId") Long storeId, Pageable pageable);

    @Query("SELECT o FROM Order o " +
           "WHERE o.userId = :userId " +
           "ORDER BY o.orderedAt DESC")
    Page<Order> findAllByUserIdWithPagination(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT o FROM Order o " +
           "ORDER BY o.orderedAt DESC")
    Page<Order> findAllWithPagination(Pageable pageable);

    /**
     * 특정 상태의 주문들 조회 (배달 가능한 주문들)
     */
    @Query("SELECT o FROM Order o " +
            "WHERE o.status = :status " +
            "ORDER BY o.cookingCompletedAt ASC")
    List<Order> findByStatus(@Param("status") OrderStatus status);

    /**
     * 특정 가게의 특정 상태 주문들 조회
     */
    @Query("SELECT o FROM Order o " +
            "WHERE o.storeId = :storeId AND o.status = :status " +
            "ORDER BY o.cookingCompletedAt ASC")
    List<Order> findByStoreIdAndStatus(@Param("storeId") Long storeId, @Param("status") OrderStatus status);

    Optional<Order> findTopByUserIdOrderByOrderedAtDesc(Long userId);

    /**
     * 가게별 오늘 주문 개수 조회
     */
    @Query("SELECT COUNT(o) FROM Order o WHERE o.storeId = :storeId " +
            "AND FUNCTION('DATE', o.createdAt) = CURRENT_DATE")
    Long countTodayOrdersByStoreId(@Param("storeId") Long storeId);

    /**
     * 가게별 오늘 매출 조회 (배달 완료된 주문만)
     */
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.storeId = :storeId " +
            "AND FUNCTION('DATE', o.createdAt) = CURRENT_DATE " +
            "AND o.status = 'DELIVERED'")
    BigDecimal getTodayRevenueByStoreId(@Param("storeId") Long storeId);

    /**
     * 가게별 총 주문 개수 조회
     */
    @Query("SELECT COUNT(o) FROM Order o WHERE o.storeId = :storeId")
    Long countTotalOrdersByStoreId(@Param("storeId") Long storeId);

    /**
     * 가게별 최근 주문 조회 (최대 N개)
     */
    @Query("SELECT o FROM Order o WHERE o.storeId = :storeId " +
            "ORDER BY o.createdAt DESC")
    List<Order> findRecentOrdersByStoreId(@Param("storeId") Long storeId, Pageable pageable);
}
