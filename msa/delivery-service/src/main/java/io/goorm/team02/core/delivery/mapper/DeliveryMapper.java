package io.goorm.team02.core.delivery.mapper;

import io.goorm.team02.core.delivery.entity.Delivery;
import io.goorm.team02.dto.deliveries.DeliveryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface DeliveryMapper {

    @Mapping(source = "deliveryAddress", target = "completeAddress")
    DeliveryResponse toResponse(Delivery delivery);


}