package io.goorm.team02.core.users.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserAddressRequest {
    private String addressName;   // 집, 회사
    private String address;       // 실제 주소지
    private String detailAddress; // 동, 호수
    private String zipcode;       // 우편번호
    private BigDecimal latitude;  // 위도
    private BigDecimal longitude; // 경도
    private Boolean isDefault;    // 기본 배송지 여부

}

