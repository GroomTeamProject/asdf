package io.goorm.team02.core.auth.controller.dto;

//LoginResponse로 { "token": "JWT값" } 반환
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import io.goorm.team02.core.users.domain.enums.UserType;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String email;
    private String name;
    private UserType userType;
    private String token;
}
