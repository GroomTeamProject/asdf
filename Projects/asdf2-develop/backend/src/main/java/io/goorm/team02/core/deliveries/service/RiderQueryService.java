package io.goorm.team02.core.deliveries.service;

import io.goorm.team02.core.deliveries.domain.Delivery;
import io.goorm.team02.core.deliveries.domain.enums.DeliveryStatus;
import io.goorm.team02.core.deliveries.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RiderQueryService {
    private final DeliveryRepository deliveryRepository;

    public Long getTodayIncome(Long riderId) {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay();
        Long income = deliveryRepository.sumFeeByRiderAndDate(riderId,
                start,end, DeliveryStatus.DELIVERED);
        return income != null ? income : 0;
    }

    public Long getTodayCount(Long riderId) {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay();
        Long count = deliveryRepository.countByRiderId(riderId,start,end, DeliveryStatus.DELIVERED);
        return count != null ? count : 0;
    }

    public Long getTodayAvgMinutes(Long riderId) {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay();
        Long avg = deliveryRepository.findAvgDeliveryMinutes(riderId,start,end, DeliveryStatus.DELIVERED.name());
        return avg != null ? avg : 0;
    }

    public Delivery getCurrentDelivery(Long riderId) {
        var currentDelivery = deliveryRepository.findByRiderIdAndStatusIn(riderId, List.of(DeliveryStatus.PICKED_UP,DeliveryStatus.ACCEPTED));
        return currentDelivery.orElse(null);
    }
}
