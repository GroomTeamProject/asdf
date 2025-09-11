package io.goorm.team02.core.users.controller.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class UserAddressRequest {
    private String addressName;    // 집, 회사 등
    private String address;        // 실제 주소
    private String detailAddress;  // 상세주소
    private String zipcode;
    private Boolean isDefault;     // 기본 주소 여부
}
