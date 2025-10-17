package io.goorm.team02.users.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
//import io.goorm.team02.core.users.domain.enums.UserType;

@Getter
@AllArgsConstructor
public class LoginResponse {
    private Long id;
    private String email;
    private String name;
    private String userType;
    private String token;
    private String refreshtoken;
}
