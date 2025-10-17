package io.goorm.team02.users.controller.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequest {
    private String email;
    private String password;
    private String passwordCheck;
    private String name;
    private String phone;
    //private UserType userType;
    private String userType; // 문자열로 받아서 user-service에서 UserType으로 변환

    // 주소 정보
    private String addressName;    // "집", "회사" 등
    private String address;        // 기본 주소
    private String detailAddress;  // 상세 주소
    private String zipcode;
    private Boolean isDefault = true; // 기본 배송지 여부 , 첫 주소는 기본 주소
}