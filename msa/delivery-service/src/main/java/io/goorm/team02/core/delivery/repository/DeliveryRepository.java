package io.goorm.team02.core.delivery.repository;

import io.goorm.team02.core.delivery.entity.Delivery;
import io.goorm.team02.core.delivery.entity.enums.DeliveryStatus;
import io.goorm.team02.dto.deliveries.DeliveryResponse;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DeliveryRepository extends JpaRepository<Delivery,Long> {
    @Query("SELECT COUNT(d.id) FROM Delivery d " +
            "WHERE d.riderId = :riderId " +
            "AND d.deliveredAt >= :start " +
            "AND d.deliveredAt < :end " +
            "AND d.status = :status")
    BigDecimal countByRiderId(@Param("riderId") Long riderId,
                        @Param("start") LocalDateTime start,
                        @Param("end") LocalDateTime end,
                        @Param("status") DeliveryStatus status);

    @Query("SELECT SUM(d.deliveryFee) FROM Delivery d " +
            "WHERE d.riderId = :riderId " +
            "AND d.deliveredAt >= :start " +
            "AND d.deliveredAt < :end " +
            "AND d.status = :status")
    BigDecimal sumFeeByRiderAndDate(@Param("riderId") Long riderId,
                                    @Param("start") LocalDateTime start,
                                    @Param("end") LocalDateTime end,
                                    @Param("status") DeliveryStatus status);


    @Query(value = "SELECT FLOOR(AVG(TIMESTAMPDIFF(MINUTE, d.accepted_at, d.delivered_at))) " +
            "FROM deliveries d " +
            "WHERE d.rider_id = :riderId " +
            "AND d.delivered_at >= :start " +
            "AND d.delivered_at < :end " +
            "AND d.status = :status",
            nativeQuery = true
    )
    Long findAvgDeliveryMinutes(@Param("riderId" ) Long riderId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end, @Param("status") String status);


    @Query("SELECT d FROM Delivery d " +
            "WHERE d.riderId = :riderId")
    List<Delivery> findDeliveriesByRiderId(@Param("riderId") Long riderId);

    @Query("""
    SELECT COUNT(d.id)
    FROM Delivery d
    WHERE d.riderId = :riderId
    AND d.status <> 'DELIVERED'
""")
    Long notDeliveredCountByRiderId(@Param("riderId") Long riderId);

    Optional<Delivery> findByOrderId(Long orderId);

    Optional<Delivery> findByRiderIdAndOrderIdAndStatus(Long riderId, Long orderId, String status);

    Optional<Delivery> findByRiderIdAndStatusIn(Long riderId, List<String> statuses);

}