package io.goorm.team02.core.auth.controller.dto;
import lombok.Getter;
import lombok.Setter;
import io.goorm.team02.core.users.domain.enums.UserType;

@Getter
@Setter
public class SignupRequest {
    private String email;
    private String password;
    private String passwordCheck;
    private String name;
    private String phone;
    private UserType userType;

    // 주소 정보
    private String addressName;    // "집", "회사" 등
    private String address;        // 기본 주소
    private String detailAddress;  // 상세 주소
    private String zipcode;
    private Boolean isDefault = true; // 기본 배송지 여부 , 첫 주소는 기본 주소
}
