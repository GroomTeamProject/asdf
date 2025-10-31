package io.goorm.team02.core.deliveries.service;

import io.goorm.team02.core.deliveries.controller.dto.DeliveryResponse;
import io.goorm.team02.core.deliveries.domain.Delivery;
import io.goorm.team02.core.deliveries.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RiderDeliveryHistoryService {

    private final DeliveryRepository deliveryRepository;

    public List<DeliveryResponse> getDeliveries(Long riderId) {

        return deliveryRepository.findDeliveriesByRiderId(riderId);
    }
}
