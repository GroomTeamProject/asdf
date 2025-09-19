package io.goorm.team02.core.users.controller.dto;

import io.goorm.team02.core.users.domain.UserRole;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class UserRoleAddRequest {
    private String role;

    /*public UserRole.Role getRole() {
        return role;
    }

    public void setRole(UserRole.Role role) {
        this.role = role;
    }*/
}
