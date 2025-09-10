package io.goorm.team02.core.auth.controller.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    private String email;  // User 엔티티 기준
    private String password;
}
