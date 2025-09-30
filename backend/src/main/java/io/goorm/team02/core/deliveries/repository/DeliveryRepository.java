package io.goorm.team02.core.deliveries.repository;

import io.goorm.team02.core.deliveries.controller.dto.DeliveryResponse;
import io.goorm.team02.core.deliveries.domain.Delivery;
import io.goorm.team02.core.deliveries.domain.enums.DeliveryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface DeliveryRepository extends JpaRepository<Delivery,Long> {

    @Query("SELECT COUNT (d.id) FROM Delivery d " +
            "WHERE d.rider.id = :riderId " +
            "AND d.deliveredAt >= :start " +
            "AND d.deliveredAt < :end " +
            "AND d.status = :status")
    Long countByRiderId(@Param("riderId") Long riderId,
                        @Param("start") LocalDateTime start,
                        @Param("end") LocalDateTime end,
                        @Param("status") DeliveryStatus status);


    @Query("SELECT SUM(d.deliveryFee) FROM Delivery d " +
            "WHERE d.rider.id = :riderId " +
            "AND d.deliveredAt >= :start " +
            "AND d.deliveredAt < :end " +
            "AND d.status = :status")
    Long sumFeeByRiderAndDate(@Param("riderId") Long riderId,
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

    Delivery findByOrderId(Long orderId);

    Optional<Delivery> findByRiderIdAndStatus(Long riderId, DeliveryStatus status);


    @Query("SELECT d FROM Delivery d " +
            "WHERE d.rider.id = :riderId " +
            "AND d.status IN :statuses")
    Optional<Delivery> findByRiderIdAndStatusIn(@Param("riderId") Long riderId,
                                                @Param("statuses") Collection<DeliveryStatus> statuses);



    @Query("select new io.goorm.team02.core.deliveries.controller.dto.DeliveryResponse(" +
            "d.id, o.id, r.id, d.pickupAddress, d.deliveryAddress, " +
            "d.estimatedTime, d.distanceKm, d.acceptedAt, d.pickedUpAt, d.deliveredAt, d.deliveryFee) " +
            "from Delivery d " +
            "join d.order o " +
            "left join d.rider r " +
            "where r.id = :riderId")
    List<DeliveryResponse> findDeliveriesByRiderId(@Param("riderId") Long riderId);

}
