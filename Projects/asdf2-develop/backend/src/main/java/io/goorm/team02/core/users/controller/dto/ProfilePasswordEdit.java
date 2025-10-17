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
