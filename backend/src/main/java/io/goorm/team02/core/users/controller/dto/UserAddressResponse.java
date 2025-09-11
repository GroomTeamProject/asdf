package io.goorm.team02.core.users.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor   // 전체 필드 생성자
@NoArgsConstructor    // 기본 생성자
public class UserAddressResponse {
    private Long id;
    private String addressName;
    private String address;
    private String detailAddress;
    private String zipcode;
    private Boolean isDefault;
}
