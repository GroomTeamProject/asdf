package io.goorm.team02.core.stores.controller.dto.storemanagement;

import io.goorm.team02.core.stores.domain.enums.StoreCategory;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StoreCreateRequest {

    private String businessNumber;
    private String name;
    private String description;
    private String phone;
    private String address;
    private String detailAddress;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private StoreCategory category;
    private BigDecimal minOrderAmount;
    private BigDecimal deliveryFee;
    private Integer deliveryTimeMin;
    private Integer deliveryTimeMax;
    private String imageUrl;
}