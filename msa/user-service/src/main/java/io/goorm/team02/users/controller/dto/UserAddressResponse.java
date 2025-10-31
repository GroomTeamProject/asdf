package io.goorm.team02.users.controller.dto;

import io.goorm.team02.users.domain.UserAddress;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAddressResponse {
    
    private Long id;
    private Long userId;
    private String addressName;
    private String address;
    private String detailAddress;
    private String zipcode;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Boolean isDefault;
    
    // UserAddress 엔티티에서 UserAddressResponse로 변환
    public static UserAddressResponse from(UserAddress userAddress) {
        return UserAddressResponse.builder()
                .id(userAddress.getId())
                .userId(userAddress.getUser().getId())
                .addressName(userAddress.getAddressName())
                .address(userAddress.getAddress())
                .detailAddress(userAddress.getDetailAddress())
                .zipcode(userAddress.getZipcode())
                .latitude(userAddress.getLatitude())
                .longitude(userAddress.getLongitude())
                .isDefault(userAddress.getIsDefault())
                .build();
    }

}



