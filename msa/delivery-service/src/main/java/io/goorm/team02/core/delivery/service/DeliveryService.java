package io.goorm.team02.core.delivery.service;

import io.goorm.team02.core.delivery.client.OrderServiceClient;
import io.goorm.team02.core.delivery.entity.Delivery;
import io.goorm.team02.core.delivery.entity.enums.DeliveryStatus;
import io.goorm.team02.core.delivery.mapper.DeliveryMapper;
import io.goorm.team02.core.delivery.repository.DeliveryRepository;
import io.goorm.team02.dto.deliveries.DeliveryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final OrderServiceClient orderServiceClient;
    private final DeliveryMapper deliveryMapper;

    public Delivery accept(Long riderId, Long orderId) {
        if (deliveryRepository.findByOrderId(orderId).isPresent()) {
            throw new IllegalArgumentException("The order is already accepted: " + orderId);
        }

        Delivery delivery = Delivery.accept(orderServiceClient.getOrderDetailForDelivery(orderId), riderId);
        return deliveryRepository.save(delivery);
    }

    public Delivery pickup(Long riderId, Long orderId) {
        Delivery delivery = deliveryRepository.findByRiderIdAndOrderIdAndStatus(
                riderId, orderId, DeliveryStatus.ACCEPTED.name()
        ).orElseThrow(() -> new NoSuchElementException("Delivery not found or not in ACCEPTED state"));

        delivery.setStatus(DeliveryStatus.PICKED_UP);
        delivery.setPickedUpAt(LocalDateTime.now());
        return deliveryRepository.save(delivery);
    }

    public Delivery complete(Long riderId, Long orderId) {
        Delivery delivery = deliveryRepository.findByRiderIdAndOrderIdAndStatus(
                riderId, orderId, DeliveryStatus.PICKED_UP.name()
        ).orElseThrow(() -> new NoSuchElementException("Delivery not found or not in PICKED_UP state"));

        delivery.setStatus(DeliveryStatus.DELIVERED);
        delivery.setDeliveredAt(LocalDateTime.now());
        return deliveryRepository.save(delivery);
    }

    public BigDecimal getTodayCount(Long riderId) {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay();
        BigDecimal count = deliveryRepository.countByRiderId(riderId, start, end, DeliveryStatus.ACCEPTED);
        return count != null ? count : BigDecimal.valueOf(0);
    }

    public BigDecimal getTodayIncome(Long riderId) {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay();
        BigDecimal income = deliveryRepository.sumFeeByRiderAndDate(riderId, start, end, DeliveryStatus.DELIVERED);
        return income != null ? income : BigDecimal.valueOf(0);
    }

    public Long getTodayAvg(Long riderId) {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay();
        Long avg = deliveryRepository.findAvgDeliveryMinutes(riderId, start, end, DeliveryStatus.DELIVERED.name());
        return avg != null ? avg : 0;
    }

    public String getRiderStatus(Long riderId) {
        long cnt = Optional.ofNullable(deliveryRepository.notDeliveredCountByRiderId(riderId)).orElse(0L);
        return cnt > 0 ? "BAD" : "GOOD";
    }

    public List<DeliveryResponse> getDeliveries(Long riderId) {
        return deliveryRepository.findDeliveriesByRiderId(riderId)
                .stream()
                .map(deliveryMapper::toResponse)
                .toList();
    }


    public Optional<Delivery> getCurrentDelivery(Long riderId) {
        return deliveryRepository.findByRiderIdAndStatusIn(
                riderId,
                List.of(DeliveryStatus.PICKED_UP.name(), DeliveryStatus.ACCEPTED.name())
        );
    }
}
