package io.goorm.team02.core.owner.stores.repository;

//import io.goorm.team02.core.owner.orders.domain.Order;
import io.goorm.team02.core.owner.stores.domain.Store;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {

    /**
     * 사업자번호로 가게 존재 여부 확인
     */
    boolean existsByBusinessNumber(String businessNumber);

    /**
     * 사장님 ID로 활성화된 가게 존재 여부 확인
     */
    boolean existsByOwnerIdAndIsActiveTrue(Long ownerId);

    /**
     * 사장님 ID로 활성화된 가게 조회
     */
    Optional<Store> findByOwnerIdAndIsActiveTrue(Long ownerId);

    // StoreRepository.java 수정

//    /**
//     * 가게별 오늘 주문 개수 조회
//     */
//    @Query("SELECT COUNT(o) FROM Order o WHERE o.store.id = :storeId " +
//            "AND FUNCTION('DATE', o.orderedAt) = CURRENT_DATE")
//    long countTodayOrdersByStoreId(@Param("storeId") Long storeId);
//
//    /**
//     * 가게별 오늘 매출 조회 (배달 완료된 주문만)
//     */
//    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.store.id = :storeId " +
//            "AND FUNCTION('DATE', o.orderedAt) = CURRENT_DATE " +
//            "AND o.status = io.goorm.team02.core.orders.domain.enums.OrderStatus.DELIVERED")
//    BigDecimal getTodayRevenueByStoreId(@Param("storeId") Long storeId);
//
//    /**
//     * 가게별 총 주문 개수 조회
//     */
//    @Query("SELECT COUNT(o) FROM Order o WHERE o.store.id = :storeId")
//    long countTotalOrdersByStoreId(@Param("storeId") Long storeId);

//    /**
//     * 가게별 최근 주문 조회 (최대 N개)
//     */
//    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.user LEFT JOIN FETCH o.store " +
//            "WHERE o.store.id = :storeId ORDER BY o.orderedAt DESC")
//    List<Order> findRecentOrdersByStoreId(@Param("storeId") Long storeId,
//                                          org.springframework.data.domain.Pageable pageable);
//
//    /**
//     * 가게별 특정 상태 주문 조회
//     */
//    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.user LEFT JOIN FETCH o.store " +
//            "WHERE o.store.id = :storeId AND o.status = :status " +
//            "ORDER BY o.orderedAt DESC")
//    List<Order> findOrdersByStoreIdAndStatus(@Param("storeId") Long storeId,
//                                             @Param("status") io.goorm.team02.core.orders.domain.enums.OrderStatus status);
//
//    /**
//     * 가게별 여러 상태 주문 조회
//     */
//    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.user LEFT JOIN FETCH o.store " +
//            "WHERE o.store.id = :storeId AND o.status IN :statuses " +
//            "ORDER BY o.orderedAt DESC")
//    List<Order> findOrdersByStoreIdAndStatusIn(@Param("storeId") Long storeId,
//                                               @Param("statuses") java.util.List<io.goorm.team02.core.orders.domain.enums.OrderStatus> statuses);


    // ================================
    // Customer API Methods
    // ================================

    /**
     * 활성화된 모든 가게 조회 (고객용)
     */
    List<Store> findByIsActiveTrue();

    /**
     * ID로 활성화된 가게 조회 (고객용)
     */
    Optional<Store> findByIdAndIsActiveTrue(Long id);

    /**
     * ID로 활성화된 가게 존재 여부 확인 (고객용)
     */
    boolean existsByIdAndIsActiveTrue(Long id);
}