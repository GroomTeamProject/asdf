package io.goorm.team02.core.stores.controller.dto.storemanagement;

import io.goorm.team02.core.stores.domain.Store;
import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;

@Getter
@Builder
public class StoreResponse {
    private Long id;
    private String businessNumber;
    private String name;
    private String description;
    private String phone;
    private String address;
    private String detailAddress;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String category;
    private BigDecimal minOrderAmount;
    private BigDecimal deliveryFee;
    private Integer deliveryTimeMin;
    private Integer deliveryTimeMax;
    private String imageUrl;
    private String status;
    private BigDecimal rating;
    private Integer reviewCount;
    private Boolean isActive;

    public static StoreResponse from(Store store) {
        return StoreResponse.builder()
            .id(store.getId())
            .businessNumber(store.getBusinessNumber())
            .name(store.getName())
            .description(store.getDescription())
            .phone(store.getPhone())
            .address(store.getAddress())
            .detailAddress(store.getDetailAddress())
            .latitude(store.getLatitude())
            .longitude(store.getLongitude())
            .category(store.getCategory() != null ? store.getCategory().toString() : null)
            .minOrderAmount(store.getMinOrderAmount())
            .deliveryFee(store.getDeliveryFee())
            .deliveryTimeMin(store.getDeliveryTimeMin())
            .deliveryTimeMax(store.getDeliveryTimeMax())
            .imageUrl(store.getImageUrl())
            .status(store.getStatus() != null ? store.getStatus().toString() : null)
            .rating(store.getRating())
            .reviewCount(store.getReviewCount())
            .isActive(store.getIsActive())
            .build();
    }
}