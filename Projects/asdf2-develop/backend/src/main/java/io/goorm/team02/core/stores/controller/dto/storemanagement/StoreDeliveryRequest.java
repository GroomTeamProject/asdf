package io.goorm.team02.core.stores.controller.dto.storemanagement;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StoreDeliveryRequest {

    private BigDecimal deliveryFee;
    private BigDecimal minOrderAmount;
    private Integer deliveryTimeMin;
    private Integer deliveryTimeMax;
    //private BigDecimal deliveryRadius;
}