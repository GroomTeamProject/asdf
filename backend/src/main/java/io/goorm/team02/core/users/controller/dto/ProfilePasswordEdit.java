// 마이페이지에-> 내정보(profile)-> 비밀번호 변경
package io.goorm.team02.core.users.controller.dto;
import lombok.Data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfilePasswordEdit {
    private String currentPassword;
    private String newPassword;

}