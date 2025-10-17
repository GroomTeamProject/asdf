package io.goorm.team02.core.auth.controller.dto;

//LoginResponse로 { "token": "JWT값" } 반환
import lombok.AllArgsConstructor;
import lombok.Getter;
import io.goorm.team02.core.users.domain.enums.UserType;

@Getter
@AllArgsConstructor
public class LoginResponse {
    private Long id;
    private String email;
    private String name;
    private UserType userType;
    private String token;
    private String refreshtoken;
}
