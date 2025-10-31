package io.goorm.team02.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
// 회원가입시 response
public class SignupResponse {
    private String email;
    private String name;
    private String userType;
}
