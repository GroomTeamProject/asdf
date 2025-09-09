// 마이페이지 수정 (put)
package io.goorm.team02.core.users.controller.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileUpdateRequest {
    private String email;
    private String name;
    private String phone;
}