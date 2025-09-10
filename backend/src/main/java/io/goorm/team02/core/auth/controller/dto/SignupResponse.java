package io.goorm.team02.core.auth.controller.dto;

import io.goorm.team02.core.users.domain.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
// 회원가입시 response
public class SignupResponse {
    private String email;
    private String name;
    private UserType userType;
}
